import { useMemo, useState } from 'react'
import './App.css'

const API_BASE = import.meta.env.VITE_API_BASE || 'http://localhost:8080'

function formatDate(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return new Intl.DateTimeFormat('en-US', {
    weekday: 'long',
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  }).format(date)
}

function App() {
  const [joinCode, setJoinCode] = useState('')
  const [occurrence, setOccurrence] = useState(null)
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState('')
  const [name, setName] = useState('')
  const [email, setEmail] = useState('')
  const [emailOptIn, setEmailOptIn] = useState(true)
  const [voteStatus, setVoteStatus] = useState('')

  const subActivities = useMemo(() => occurrence?.subActivities || [], [occurrence])

  async function handleJoin(event) {
    event.preventDefault()
    if (!joinCode.trim()) {
      setError('Paste a join code to continue.')
      return
    }
    setIsLoading(true)
    setError('')
    setVoteStatus('')
    try {
      const response = await fetch(`${API_BASE}/api/public/join/${joinCode.trim()}`)
      if (!response.ok) {
        throw new Error('We could not find that event. Check the code.')
      }
      const data = await response.json()
      setOccurrence(data)
    } catch (err) {
      setOccurrence(null)
      setError(err.message)
    } finally {
      setIsLoading(false)
    }
  }

  async function handleVote(subActivityId, yesNo) {
    if (!occurrence) return
    if (!name.trim()) {
      setVoteStatus('Add your name before voting.')
      return
    }
    if (emailOptIn && !email.trim()) {
      setVoteStatus('Add an email to receive reminders.')
      return
    }
    setIsLoading(true)
    setVoteStatus('')
    try {
      const payload = {
        subActivityId,
        participantName: name.trim(),
        participantEmail: email.trim() || null,
        yesNo,
        emailOptIn,
      }
      const response = await fetch(`${API_BASE}/api/public/occurrences/${occurrence.id}/votes`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(payload),
      })
      if (!response.ok) {
        throw new Error('Vote failed. Try again in a moment.')
      }
      const updated = await fetch(`${API_BASE}/api/public/join/${occurrence.joinCode}`)
      if (updated.ok) {
        const data = await updated.json()
        setOccurrence(data)
      }
      setVoteStatus('Vote saved. See you there!')
    } catch (err) {
      setVoteStatus(err.message)
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className="page">
      <header className="hero">
        <div className="hero-text">
          <span className="eyebrow">Gathering</span>
          <h1>Stop the weekly chaos. Lock in your crew in seconds.</h1>
          <p>
            Create recurring events, share one join code, and collect yes/no votes per activity
            — soccer, dinner, whatever.
          </p>
          <div className="hero-actions">
            <a className="btn primary" href="#join">Join an event</a>
            <a className="btn ghost" href="#features">How it works</a>
          </div>
          <div className="hero-metrics">
            <div>
              <strong>2 clicks</strong>
              <span>to say yes/no</span>
            </div>
            <div>
              <strong>0 logins</strong>
              <span>for your team</span>
            </div>
            <div>
              <strong>1 code</strong>
              <span>per session</span>
            </div>
          </div>
        </div>
        <div className="hero-card">
          <form onSubmit={handleJoin} className="join-card">
            <div className="card-head">
              <h2>Join by code</h2>
              <p>Paste the event code to see activities and RSVP.</p>
            </div>
            <label className="field">
              <span>Join code</span>
              <input
                value={joinCode}
                onChange={(event) => setJoinCode(event.target.value)}
                placeholder="AB12CD34"
              />
            </label>
            <button className="btn primary" type="submit" disabled={isLoading}>
              {isLoading ? 'Loading...' : 'Find event'}
            </button>
            {error && <p className="status error">{error}</p>}
          </form>
        </div>
      </header>

      <section id="features" className="features">
        <div className="feature">
          <h3>Recurring templates</h3>
          <p>Create a weekly or daily activity once and reuse it forever.</p>
        </div>
        <div className="feature">
          <h3>Sub-activities</h3>
          <p>Split events into parts (soccer, dinner, drinks) with separate votes.</p>
        </div>
        <div className="feature">
          <h3>Opt-in reminders</h3>
          <p>People who want nudges get an email 24 hours before the event.</p>
        </div>
      </section>

      <section id="join" className="join">
        <div className="join-info">
          <h2>RSVP with confidence</h2>
          <p>
            Share a single code in your group chat. Everyone votes per activity, and you
            instantly see the headcount.
          </p>
          <div className="join-badges">
            <span>No accounts</span>
            <span>Yes / No only</span>
            <span>Fast rollups</span>
          </div>
        </div>
        <div className="join-panel">
          {!occurrence && (
            <div className="empty-state">
              <h3>Paste a join code to start</h3>
              <p>We will show activities and let you vote right away.</p>
            </div>
          )}

          {occurrence && (
            <div className="event-panel">
              <div className="event-head">
                <div>
                  <span className="tag">{occurrence.templateName}</span>
                  <h3>{formatDate(occurrence.occurrenceDate)}</h3>
                  <p>{occurrence.startTime} • {occurrence.timezone}</p>
                </div>
                <div className="code-pill">Code: {occurrence.joinCode}</div>
              </div>

              <div className="participant">
                <label className="field">
                  <span>Your name</span>
                  <input value={name} onChange={(event) => setName(event.target.value)} placeholder="Lisandro" />
                </label>
                <label className="field">
                  <span>Email (optional)</span>
                  <input value={email} onChange={(event) => setEmail(event.target.value)} placeholder="you@email.com" />
                </label>
                <label className="checkbox">
                  <input
                    type="checkbox"
                    checked={emailOptIn}
                    onChange={(event) => setEmailOptIn(event.target.checked)}
                  />
                  Email me a reminder 24h before
                </label>
              </div>

              <div className="activities">
                {subActivities.map((activity) => (
                  <div className="activity" key={activity.id}>
                    <div>
                      <h4>{activity.name}</h4>
                      <p>{activity.yesCount} yes • {activity.noCount} no</p>
                    </div>
                    <div className="activity-actions">
                      <button className="btn ghost" onClick={() => handleVote(activity.id, true)} disabled={isLoading}>
                        I&apos;m in
                      </button>
                      <button className="btn outline" onClick={() => handleVote(activity.id, false)} disabled={isLoading}>
                        Can&apos;t
                      </button>
                    </div>
                  </div>
                ))}
              </div>
              {voteStatus && <p className="status">{voteStatus}</p>}
            </div>
          )}
        </div>
      </section>

      <section className="cta">
        <div>
          <h2>Make organizing feel effortless.</h2>
          <p>Create the template once, share a code every week, and stop chasing replies.</p>
        </div>
        <a className="btn primary" href="#join">Try a join code</a>
      </section>

      <footer className="footer">
        <span>Gathering MVP • Built for quick sports meetups</span>
      </footer>
    </div>
  )
}

export default App
