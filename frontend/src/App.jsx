import { useState } from 'react'
import { QRCodeCanvas } from 'qrcode.react'

function App() {
  const [originalUrl, setOriginalUrl] = useState('')
  const [customCode, setCustomCode] = useState('')
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState(null)
  const [result, setResult] = useState(null)
  const [copied, setCopied] = useState(false)

  const handleSubmit = async (e) => {
    e.preventDefault()
    setIsLoading(true)
    setError(null)
    setResult(null)
    setCopied(false)

    try {
      const payload = { originalUrl };
      if (customCode.trim()) {
        payload.customCode = customCode.trim();
      }

      const response = await fetch('/api/urls', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(payload),
      })

      const data = await response.json()

      if (!response.ok) {
        if (response.status === 400 && data.details) {
          const firstError = Object.values(data.details)[0]
          throw new Error(firstError || 'Validation failed')
        }
        throw new Error(data.message || 'Something went wrong')
      }

      setResult(data)
      setOriginalUrl('')
      setCustomCode('')
    } catch (err) {
      setError(err.message)
    } finally {
      setIsLoading(false)
    }
  }

  const handleCopy = () => {
    if (result?.shortUrl) {
      navigator.clipboard.writeText(result.shortUrl)
      setCopied(true)
      setTimeout(() => setCopied(false), 2000)
    }
  }

  return (
    <div className="glass-panel">
      <h1>QuickLink</h1>
      <p className="subtitle">Shorten, share, and track your links effortlessly.</p>

      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="originalUrl">Destination URL *</label>
          <input
            id="originalUrl"
            type="url"
            placeholder="https://example.com/very-long-url"
            value={originalUrl}
            onChange={(e) => setOriginalUrl(e.target.value)}
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="customCode">Custom Alias (Optional)</label>
          <input
            id="customCode"
            type="text"
            placeholder="e.g. my-campaign"
            value={customCode}
            onChange={(e) => setCustomCode(e.target.value)}
          />
        </div>

        {error && (
          <div className="error-message" style={{ marginBottom: '1rem' }}>
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <circle cx="12" cy="12" r="10"></circle>
              <line x1="12" y1="8" x2="12" y2="12"></line>
              <line x1="12" y1="16" x2="12.01" y2="16"></line>
            </svg>
            {error}
          </div>
        )}

        <button type="submit" className="primary-btn" disabled={isLoading || !originalUrl}>
          {isLoading ? (
            <>
              <svg className="spinner" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" style={{ animation: 'spin 1s linear infinite' }}>
                <line x1="12" y1="2" x2="12" y2="6"></line>
                <line x1="12" y1="18" x2="12" y2="22"></line>
                <line x1="4.93" y1="4.93" x2="7.76" y2="7.76"></line>
                <line x1="16.24" y1="16.24" x2="19.07" y2="19.07"></line>
                <line x1="2" y1="12" x2="6" y2="12"></line>
                <line x1="18" y1="12" x2="22" y2="12"></line>
                <line x1="4.93" y1="19.07" x2="7.76" y2="16.24"></line>
                <line x1="16.24" y1="7.76" x2="19.07" y2="4.93"></line>
              </svg>
              Generating...
            </>
          ) : (
            'Shorten URL'
          )}
        </button>
      </form>

      {result && (
        <div className="result-card">
          <div className="result-header">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
              <polyline points="22 4 12 14.01 9 11.01"></polyline>
            </svg>
            Successfully created!
          </div>
          
          <div className="short-url-container">
            <div className="short-url-display">
              {result.shortUrl}
            </div>
            <button className="copy-btn" onClick={handleCopy}>
              {copied ? 'Copied!' : 'Copy'}
            </button>
          </div>

          <div style={{ display: 'flex', justifyContent: 'center', margin: '1.5rem 0' }}>
            <div style={{ background: 'white', padding: '1rem', borderRadius: '8px' }}>
              <QRCodeCanvas 
                value={result.shortUrl} 
                size={128} 
                level={"H"} 
                includeMargin={false}
              />
            </div>
          </div>

          <div className="stats-grid">
            <div className="stat-box">
              <span className="stat-label">Clicks</span>
              <span className="stat-value">{result.clickCount}</span>
            </div>
            <div className="stat-box">
              <span className="stat-label">Created At</span>
              <span className="stat-value">
                {new Date(result.createdAt).toLocaleDateString()}
              </span>
            </div>
          </div>
        </div>
      )}
      
      <style>{`
        @keyframes spin {
          100% { transform: rotate(360deg); }
        }
      `}</style>
    </div>
  )
}

export default App
