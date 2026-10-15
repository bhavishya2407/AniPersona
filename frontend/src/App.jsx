import {
    Link,
    NavLink,
    Route,
    Routes,
    Navigate,
    useNavigate,
    useParams
} from 'react-router-dom';

import { Component, useEffect, useRef, useState } from 'react';
import { askCharacter, getCharacters, getUniverses } from './api';
import { useRoleplay } from './roleplay';

const imageFiles = {
    doraemon: 'doraemon.png',
    dorami: 'dorami.png',
    nobita: 'nobita.png',
    naruto: 'naruto.png',
    sasuke: 'sasuke.png',
    kakashi: 'kakashi.png',
    luffy: 'luffy.png',
    zoro: 'zoro.png',
    nami: 'nami.png'
};

function getCharacterImage(name) {
    const key = (name || '').toLowerCase().replace(/[^a-z]/g, '');
    return `/characters/${imageFiles[key] || `${key}.png`}`;
}

function Avatar({ name, accent = '#9b7cff', small = false }) {
    const [failed, setFailed] = useState(false);

    useEffect(() => {
        setFailed(false);
    }, [name]);

    return (
        <span
            className={`avatar ${small ? 'small' : ''}`}
            style={{
                background: `linear-gradient(135deg, ${accent}, #322765)`,
                position: 'relative',
                overflow: 'hidden',
                display: 'inline-flex',
                alignItems: 'center',
                justifyContent: 'center',
                flexShrink: 0
            }}
        >
            <span>{name?.slice(0, 1) || '?'}</span>

            {!failed && (
                <img
                    src={getCharacterImage(name)}
                    alt={name || 'Character'}
                    onError={() => setFailed(true)}
                    style={{
                        position: 'absolute',
                        inset: 0,
                        width: '100%',
                        height: '100%',
                        objectFit: 'cover',
                        borderRadius: 'inherit'
                    }}
                />
            )}
        </span>
    );
}

function Nav() {
    return (
        <header>
            <Link className="brand" to="/">
                <i>✦</i> AniPersona
            </Link>

            <nav>
                <NavLink to="/">Home</NavLink>
                <NavLink to="/universes">Explore</NavLink>
                <NavLink to="/about">About</NavLink>
            </nav>

            <Link className="button ghost" to="/universes">
                Get started
            </Link>
        </header>
    );
}

const Loading = () => (
    <div className="status">Loading your universe…</div>
);

function Home() {
    return (
        <>
            <section className="hero">
                <div>
                    <p className="eyebrow">✦ AI-POWERED STORYTELLING</p>

                    <h1>
                        Step into your
                        <br />
                        <em>favourite universe.</em>
                    </h1>

                    <p className="lead">
                        Choose a character, take your place in the story, and have a
                        conversation that remembers the moment.
                    </p>

                    <div className="actions">
                        <Link className="button" to="/universes">
                            Start your adventure <b>→</b>
                        </Link>

                        <Link className="button ghost" to="/universes">
                            Explore universes
                        </Link>
                    </div>

                    <div className="metrics">
                        <span><strong>5</strong> worlds to explore</span>
                        <span><strong>22</strong> characters</span>
                        <span><strong>∞</strong> possible stories</span>
                    </div>
                </div>

                <div className="hero-art">
                    <div className="orb one"></div>
                    <div className="orb two"></div>

                    <div className="hero-card">
                        <span className="spark">✦</span>
                        <p>Every chat is a new scene.</p>
                        <strong>Who will you become?</strong>
                    </div>

                    <div className="hero-avatar">✦</div>
                </div>
            </section>

            <section className="section">
                <p className="eyebrow">HOW IT WORKS</p>
                <h2>A story begins with one choice.</h2>

                <div className="feature-grid">
                    <article>
                        <b>01</b>
                        <h3>Pick a universe</h3>
                        <p>
                            Explore worlds full of adventure, friendship, and imagination.
                        </p>
                    </article>

                    <article>
                        <b>02</b>
                        <h3>Choose your roles</h3>
                        <p>
                            Choose your AI companion and the character you want to play.
                        </p>
                    </article>

                    <article>
                        <b>03</b>
                        <h3>Make it yours</h3>
                        <p>
                            Set the scene, choose a tone, then start talking.
                        </p>
                    </article>
                </div>
            </section>
        </>
    );
}

function Universes() {
    const [items, setItems] = useState([]);
    const [query, setQuery] = useState('');
    const [error, setError] = useState('');

    useEffect(() => {
        getUniverses()
            .then(data => {
                setItems(data);
                setError('');
            })
            .catch(e => setError(e.message));
    }, []);

    const result = items.filter(x =>
        x.name.toLowerCase().includes(query.toLowerCase())
    );

    return (
        <Page
            title="Choose a universe"
            sub="Where would you like your next story to begin?"
        >
            <input
                className="search"
                value={query}
                onChange={e => setQuery(e.target.value)}
                placeholder="Search a universe…"
            />

            {error ? (
                <Error text={error} />
            ) : !items.length ? (
                <Loading />
            ) : result.length === 0 ? (
                <p>No universes found.</p>
            ) : (
                <div className="cards">
                    {result.map(x => (
                        <article
                            className="universe-card"
                            key={x.id}
                            style={{ '--accent': x.accent }}
                        >
                            <div className="universe-symbol">✦</div>
                            <p className="eyebrow">UNIVERSE</p>
                            <h2>{x.name}</h2>
                            <p>{x.description}</p>

                            <Link
                                className="text-link"
                                to={`/universes/${encodeURIComponent(String(x.id).trim())}`}
                            >
                                Explore characters <b>→</b>
                            </Link>
                        </article>
                    ))}
                </div>
            )}
        </Page>
    );
}

function Characters() {
    const { id } = useParams();
    const nav = useNavigate();
    const { state, setState } = useRoleplay();

    const [items, setItems] = useState([]);
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        let active = true;

        setLoading(true);
        setError('');
        setItems([]);

        const universeId = decodeURIComponent(id || '').trim();

        getCharacters(universeId)
            .then(data => {
                if (active) setItems(data);
            })
            .catch(e => {
                if (active) setError(e.message);
            })
            .finally(() => {
                if (active) setLoading(false);
            });

        return () => {
            active = false;
        };
    }, [id]);

    const select = person => {
        const universeId = decodeURIComponent(id || '').trim();

        const nextState = {
            ...state,
            universeId,
            ai: person,
            user: null
        };

        localStorage.setItem(
            'anipersona-roleplay',
            JSON.stringify(nextState)
        );

        setState(nextState);
        nav('/setup');
    };

    return (
        <Page
            title="Choose your AI companion"
            sub="Choose a character to begin your adventure."
        >
            <Link className="back" to="/universes">
                ← All universes
            </Link>

            {error ? (
                <Error text={error} />
            ) : loading ? (
                <Loading />
            ) : items.length === 0 ? (
                <p>No characters found in this universe.</p>
            ) : (
                <div className="characters">
                    {items.map(person => (
                        <article className="character" key={person.id}>
                            <Avatar
                                name={person.name}
                                accent={person.accent}
                            />

                            <div>
                                <p className="eyebrow">
                                    {(id || '').replaceAll('-', ' ')}
                                </p>

                                <h2>{person.name}</h2>
                                <p>{person.description}</p>
                                <small>{person.personality}</small>

                                <button
                                    type="button"
                                    className="button compact"
                                    onClick={() => select(person)}
                                >
                                    Choose {person.name}
                                </button>
                            </div>
                        </article>
                    ))}
                </div>
            )}
        </Page>
    );
}

function Setup() {
    const nav = useNavigate();
    const { state, setState } = useRoleplay();

    const [characters, setCharacters] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    useEffect(() => {
        if (!state.universeId || !state.ai) return;

        let active = true;

        setLoading(true);
        setError('');

        getCharacters(state.universeId)
            .then(data => {
                if (active) setCharacters(data);
            })
            .catch(e => {
                if (active) setError(e.message);
            })
            .finally(() => {
                if (active) setLoading(false);
            });

        return () => {
            active = false;
        };
    }, [state.universeId, state.ai?.id]);

    if (!state.ai) {
        return <Navigate to="/universes" replace />;
    }

    const update = (key, value) => {
        setState(previous => {
            const next = { ...previous, [key]: value };

            localStorage.setItem(
                'anipersona-roleplay',
                JSON.stringify(next)
            );

            return next;
        });
    };

    return (
        <Page
            title="Build your scene"
            sub="Choose who you’ll play, then make the roleplay your own."
        >
            <div className="setup-grid">
                <section className="setup-panel">
                    <label>Who would you like to play as?</label>

                    {loading ? (
                        <Loading />
                    ) : error ? (
                        <Error text={error} />
                    ) : (
                        <div className="role-list">
                            {characters.map(person => (
                                <button
                                    type="button"
                                    key={person.id}
                                    className={`role ${state.user?.id === person.id ? 'chosen' : ''}`}
                                    onClick={() => update('user', person)}
                                >
                                    <Avatar
                                        name={person.name}
                                        accent={person.accent}
                                        small
                                    />

                                    <span>{person.name}</span>

                                    {person.id === state.ai.id && (
                                        <small>same role allowed</small>
                                    )}
                                </button>
                            ))}
                        </div>
                    )}
                </section>

                <section className="setup-panel">
                    <label>Set the mood</label>

                    <div className="tones">
                        {[
                            'Friendly',
                            'Funny',
                            'Emotional',
                            'Adventure',
                            'Supportive'
                        ].map(tone => (
                            <button
                                type="button"
                                key={tone}
                                className={state.tone === tone ? 'chosen' : ''}
                                onClick={() => update('tone', tone)}
                            >
                                {tone}
                            </button>
                        ))}
                    </div>

                    <label>
                        Scene <small>optional</small>
                    </label>

                    <textarea
                        maxLength="800"
                        value={state.scenario || ''}
                        onChange={e => update('scenario', e.target.value)}
                        placeholder="e.g. You are sitting in Nobita's room, worried about an exam."
                    />

                    <label>Language</label>

                    <select
                        value={state.language || 'English'}
                        onChange={e => update('language', e.target.value)}
                    >
                        <option>English</option>
                        <option>Hindi</option>
                        <option>Japanese</option>
                    </select>
                </section>
            </div>

            <div className="summary">
                <span>
                    World <b>{state.universeId?.replaceAll('-', ' ')}</b>
                </span>

                <span>
                    AI <b>{state.ai.name}</b>
                </span>

                <span>
                    You <b>{state.user?.name || 'Choose a role'}</b>
                </span>

                <button
                    type="button"
                    disabled={!state.user}
                    className="button"
                    onClick={() => nav('/chat')}
                >
                    Start conversation →
                </button>
            </div>
        </Page>
    );
}

function Chat() {
    const nav = useNavigate();
    const { state } = useRoleplay();

    const [messages, setMessages] = useState([]);
    const [text, setText] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    const bottom = useRef(null);

    useEffect(() => {
        bottom.current?.scrollIntoView({ behavior: 'smooth' });
    }, [messages, loading]);

    if (!state.ai || !state.user) {
        return <Navigate to="/universes" replace />;
    }

    const send = async e => {
        e?.preventDefault();

        const content = text.trim();

        if (!content || loading) return;

        const nextMessages = [
            ...messages,
            { role: 'user', content }
        ];

        setMessages(nextMessages);
        setText('');
        setLoading(true);
        setError('');

        try {
            const data = await askCharacter({
                universeId: state.universeId,
                aiCharacterId: state.ai.id,
                userCharacterId: state.user.id,
                scenario: state.scenario || '',
                tone: state.tone || 'Friendly',
                language: state.language || 'English',
                messages: nextMessages
            });

            setMessages([
                ...nextMessages,
                { role: 'assistant', content: data.reply }
            ]);
        } catch (e) {
            setError(e.message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <main className="chat-wrap">
            <aside>
                <Link className="brand" to="/">
                    <i>✦</i> AniPersona
                </Link>

                <button
                    type="button"
                    className="back-btn"
                    onClick={() => nav(`/universes/${encodeURIComponent(state.universeId)}`)}
                >
                    ← Change character
                </button>

                <div className="partner">
                    <Avatar
                        name={state.ai.name}
                        accent={state.ai.accent}
                    />
                    <h3>{state.ai.name}</h3>
                    <p>{state.universeId.replaceAll('-', ' ')} universe</p>
                </div>

                <div className="your-role">
                    You are playing <b>{state.user.name}</b>
                </div>

                <button
                    type="button"
                    className="clear"
                    onClick={() => {
                        if (window.confirm('Clear this conversation?')) {
                            setMessages([]);
                            setError('');
                        }
                    }}
                >
                    Clear conversation
                </button>
            </aside>

            <section className="chat">
                <div className="chat-title">
                    <div>
                        <p className="eyebrow">YOUR ROLEPLAY PARTNER</p>
                        <h1>{state.ai.name}</h1>
                    </div>

                    <span className="online">● In character</span>
                </div>

                <div className="messages">
                    {!messages.length && (
                        <div className="welcome">
                            <Avatar
                                name={state.ai.name}
                                accent={state.ai.accent}
                            />
                            <h2>Your scene is ready.</h2>
                            <p>
                                Start as {state.user.name}. {state.ai.name} is listening.
                            </p>
                        </div>
                    )}

                    {messages.map((message, index) => (
                        <div
                            className={`message ${message.role}`}
                            key={index}
                        >
                            {message.role === 'assistant' && (
                                <Avatar
                                    name={state.ai.name}
                                    accent={state.ai.accent}
                                    small
                                />
                            )}

                            <div>
                                <p>{message.content}</p>
                                <small>
                                    {message.role === 'assistant'
                                        ? state.ai.name
                                        : state.user.name}{' '}
                                    · now
                                </small>
                            </div>
                        </div>
                    ))}

                    {loading && (
                        <div className="message assistant">
                            <Avatar
                                name={state.ai.name}
                                accent={state.ai.accent}
                                small
                            />

                            <div className="typing">
                                <i></i>
                                <i></i>
                                <i></i>
                            </div>
                        </div>
                    )}

                    {error && (
                        <Error
                            text={error}
                            retry={() => send()}
                        />
                    )}

                    <div ref={bottom} />
                </div>

                <form className="composer" onSubmit={send}>
                    <textarea
                        value={text}
                        onChange={e => setText(e.target.value)}
                        onKeyDown={e => {
                            if (e.key === 'Enter' && !e.shiftKey) {
                                e.preventDefault();
                                send();
                            }
                        }}
                        maxLength="2000"
                        placeholder={`Speak as ${state.user.name}…`}
                    />

                    <button
                        type="submit"
                        className="send"
                        disabled={!text.trim() || loading}
                    >
                        ↑
                    </button>
                </form>

                <small className="hint">
                    Enter to send · Shift + Enter for a new line
                </small>
            </section>
        </main>
    );
}

function Error({ text, retry }) {
    return (
        <div className="error">
            {text}
            {retry && (
                <button type="button" onClick={retry}>
                    Retry
                </button>
            )}
        </div>
    );
}

function Page({ title, sub, children }) {
    return (
        <main className="page">
            <div className="page-head">
                <p className="eyebrow">ANIPERSONA / EXPLORE</p>
                <h1>{title}</h1>
                <p>{sub}</p>
            </div>

            {children}
        </main>
    );
}

function About() {
    return (
        <Page
            title="About AniPersona"
            sub="Your imagination, your character, your story."
        >
            <section className="section">
                <h2>Step into your favourite universe.</h2>

                <p>
                    AniPersona is an AI-powered roleplay application that lets
                    you interact with your favourite fictional characters.
                    Choose a universe, select your AI companion, choose the
                    character you want to play, and create your own story.
                </p>

                <div className="feature-grid">
                    <article>
                        <h3>Choose Your World</h3>
                        <p>Explore different anime and fictional universes.</p>
                    </article>

                    <article>
                        <h3>Meet Your Companion</h3>
                        <p>Chat with AI characters that have unique personalities.</p>
                    </article>

                    <article>
                        <h3>Create Your Story</h3>
                        <p>Set the mood, choose your role, and begin your adventure.</p>
                    </article>
                </div>

                <Link className="button" to="/universes">
                    Start your adventure →
                </Link>
            </section>
        </Page>
    );
}

class ChatErrorBoundary extends Component {
    state = { error: null };

    static getDerivedStateFromError(error) {
        return { error };
    }

    render() {
        if (this.state.error) {
            return (
                <main className="page">
                    <div className="error">
                        <b>Chat screen error:</b> {this.state.error.message}

                        <br />

                        <button
                            type="button"
                            onClick={() => window.location.assign('/universes')}
                        >
                            Return to universes
                        </button>
                    </div>
                </main>
            );
        }

        return this.props.children;
    }
}

export default function App() {
    return (
        <>
            <Nav />

            <Routes>
                <Route path="/" element={<Home />} />
                <Route path="/universes" element={<Universes />} />
                <Route path="/universes/:id" element={<Characters />} />
                <Route path="/setup" element={<Setup />} />

                <Route
                    path="/chat"
                    element={
                        <ChatErrorBoundary>
                            <Chat />
                        </ChatErrorBoundary>
                    }
                />

                <Route path="/about" element={<About />} />
                <Route path="*" element={<Home />} />
            </Routes>
        </>
    );
}

