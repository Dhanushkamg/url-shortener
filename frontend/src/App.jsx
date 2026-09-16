import { useState, useEffect } from 'react'
import { QRCodeCanvas } from 'qrcode.react'
import './App.css'

// Icons
const ChartIcon = () => (
  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M3 3v18h18"/><path d="m19 9-5 5-4-4-3 3"/></svg>
);
const CopyIcon = () => (
  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><rect width="14" height="14" x="8" y="8" rx="2" ry="2"/><path d="M4 16c-1.1 0-2-.9-2-2V4c0-1.1.9-2 2-2h10c1.1 0 2 .9 2 2"/></svg>
);

function App() {
  const [originalUrl, setOriginalUrl] = useState('')
  const [isLoading, setIsLoading] = useState(false)
  const [urls, setUrls] = useState([])
  const [activeUrl, setActiveUrl] = useState(null)

  useEffect(() => {
    const fetchUrls = async () => {
      try {
        const response = await fetch('/api/urls')
        if (response.ok) {
          const data = await response.json()
          setUrls(data)
          if (data.length > 0 && !activeUrl) {
            setActiveUrl(data[0])
          }
        }
      } catch (error) {
        console.error("Failed to fetch URLs", error)
      }
    }
    fetchUrls()
  }, [])

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (!originalUrl) return
    
    setIsLoading(true)
    try {
      const payload = { originalUrl };
      const response = await fetch('/api/urls', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
      })
      const data = await response.json()
      if (response.ok) {
        setUrls([data, ...urls])
        setActiveUrl(data)
        setOriginalUrl('')
      }
    } catch (err) {
      console.error(err)
    } finally {
      setIsLoading(false)
    }
  }

  const handleCopy = (url) => {
    navigator.clipboard.writeText(url)
  }

  return (
    <div className="dashboard-container">
      {/* Navbar */}
      <nav className="navbar">
        <div className="nav-brand">
          <div className="brand-icon">Q</div>
          QuickLink
        </div>
        <div className="nav-links">
          <span className="nav-item active"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><rect width="7" height="9" x="3" y="3" rx="1"/><rect width="7" height="5" x="14" y="3" rx="1"/><rect width="7" height="9" x="14" y="12" rx="1"/><rect width="7" height="5" x="3" y="16" rx="1"/></svg> Dashboard</span>
          <span className="nav-item"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M10 13a5 5 0 0 0 7.54.54l3-3a5 5 0 0 0-7.07-7.07l-1.72 1.71"/><path d="M14 11a5 5 0 0 0-7.54-.54l-3 3a5 5 0 0 0 7.07 7.07l1.71-1.71"/></svg> Links</span>
          <span className="nav-item"><ChartIcon /> Analytics</span>
        </div>
        <div className="nav-user">
          <div className="user-info" style={{textAlign: 'right'}}>
            <span className="user-name">User</span>
            <span className="user-plan">Upgrade</span>
          </div>
          <div className="user-avatar">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
          </div>
        </div>
      </nav>

      {/* Main Grid */}
      <div className="dashboard-main">
        
        {/* Left Column */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
          
          <div style={{ marginBottom: '0.5rem' }}>
            <h1 style={{ textAlign: 'left', margin: 0, fontSize: '1.75rem', color: 'white', WebkitTextFillColor: 'white' }}>Dashboard</h1>
            <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem', marginTop: '0.25rem' }}>Welcome back!</p>
          </div>

          <form className="shorten-form" onSubmit={handleSubmit}>
            <div className="shorten-input-group">
              <input 
                type="url" 
                className="shorten-input"
                placeholder="Paste your long URL here..." 
                value={originalUrl}
                onChange={e => setOriginalUrl(e.target.value)}
                required
              />
            </div>
            <button type="submit" className="shorten-btn" disabled={isLoading || !originalUrl}>
              {isLoading ? '...' : 'Shorten Link'} <span style={{background: 'rgba(255,255,255,0.2)', padding: '2px 6px', borderRadius: '4px', fontSize: '0.8em'}}>QL</span>
            </button>
          </form>

          <div className="panel" style={{ flex: 1, minHeight: '300px' }}>
            <div className="panel-header">
              <span className="panel-title">Shortened Links <span style={{color: 'var(--text-secondary)', fontSize: '0.85rem', fontWeight: 'normal'}}>(last 30 days)</span></span>
            </div>
            
            <div className="links-table-container">
              <table className="links-table">
                <thead>
                  <tr>
                    <th>Original URL</th>
                    <th>Shortened URL</th>
                    <th>Date</th>
                    <th>Clicks</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {urls.map(url => (
                    <tr key={url.shortCode} onClick={() => setActiveUrl(url)} style={{cursor: 'pointer', background: activeUrl?.shortCode === url.shortCode ? 'rgba(255,255,255,0.05)' : ''}}>
                      <td className="td-original">{url.originalUrl}</td>
                      <td>
                        <div style={{fontSize: '0.75rem', color: 'var(--text-secondary)'}}>qk.link</div>
                        <div className="td-short">{url.shortCode}</div>
                      </td>
                      <td style={{color: 'var(--text-secondary)'}}>{new Date(url.createdAt).toLocaleDateString()}</td>
                      <td>{url.clickCount}</td>
                      <td>
                        <button className="action-btn" onClick={(e) => { e.stopPropagation(); handleCopy(url.shortUrl); }}>
                          <CopyIcon />
                        </button>
                      </td>
                    </tr>
                  ))}
                  {urls.length === 0 && (
                    <tr><td colSpan="5" style={{textAlign: 'center', color: 'var(--text-secondary)'}}>No links generated yet.</td></tr>
                  )}
                </tbody>
              </table>
            </div>
          </div>
        </div>

        {/* Right Column */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
          
          <div className="panel">
            <div className="panel-header">
              <span className="panel-title">Analytics Overview</span>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '1rem' }}>
              <div>
                <div style={{ fontSize: '0.85rem', color: 'var(--text-secondary)' }}>Total Clicks (Last 30 Days)</div>
                <div style={{ fontSize: '1.5rem', fontWeight: 'bold' }}>{urls.reduce((acc, curr) => acc + curr.clickCount, 0)}</div>
              </div>
            </div>
            {/* Mock Chart */}
            <div className="chart-placeholder">
              <div className="chart-bar" style={{ height: '30%' }}></div>
              <div className="chart-bar" style={{ height: '50%' }}></div>
              <div className="chart-bar" style={{ height: '20%' }}></div>
              <div className="chart-bar" style={{ height: '70%' }}></div>
              <div className="chart-bar" style={{ height: '40%' }}></div>
              <div className="chart-bar" style={{ height: '90%' }}></div>
              <div className="chart-bar" style={{ height: '60%' }}></div>
            </div>
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1.5rem' }}>
            <div className="panel">
              <div className="panel-header">
                <span className="panel-title">Top Performers</span>
              </div>
              <div className="performers-list">
                {urls.slice(0, 4).map(url => (
                  <div className="performer-item" key={'perf-'+url.shortCode}>
                    <div>
                      <div className="performer-url">{url.originalUrl}</div>
                      <div className="performer-short">qk.link/{url.shortCode}</div>
                    </div>
                    <div className="performer-clicks">{url.clickCount}</div>
                  </div>
                ))}
                {urls.length === 0 && <div style={{color: 'var(--text-secondary)', fontSize: '0.85rem'}}>No data</div>}
              </div>
            </div>

            <div className="panel">
              <div className="panel-header">
                <span className="panel-title">Link QR Code</span>
              </div>
              {activeUrl ? (
                <div className="qr-container">
                  <div style={{fontSize: '0.85rem', color: 'var(--text-secondary)', textAlign: 'center'}}>
                    {activeUrl.shortUrl}
                  </div>
                  <div className="qr-box">
                    <QRCodeCanvas value={activeUrl.shortUrl} size={140} level={"H"} includeMargin={false} />
                  </div>
                  <div className="qr-actions">
                    <button className="qr-btn" onClick={() => handleCopy(activeUrl.shortUrl)}>Copy</button>
                    <button className="qr-btn primary" onClick={() => window.open(activeUrl.shortUrl, '_blank')}>Visit</button>
                  </div>
                </div>
              ) : (
                <div style={{display: 'flex', flex: 1, alignItems: 'center', justifyContent: 'center', color: 'var(--text-secondary)', fontSize: '0.85rem'}}>
                  Select a link to view QR
                </div>
              )}
            </div>
          </div>

        </div>
      </div>
    </div>
  )
}

export default App

// Dashboard Component End
