# AniPersona

AniPersona is an AI-powered fictional-character roleplay platform. Select an anime universe, choose an AI character and your own role, customise the scene, then chat through local Ollama from a secure Spring Boot backend.

## What is included

- React + Vite interface with responsive selection flow and chat UX.
- Spring Boot 4 REST API on port `8081` by default.
- Five universes and 22 structured character profiles served by the API.
- A reusable server-side character prompt builder that keeps the user and AI roles distinct.
- Local Ollama AI integration. It runs on your computer and does not need an API key.
- MongoDB-ready, anonymous-session-scoped conversation endpoints.

## Architecture

`React frontend → Spring Boot REST API → Ollama / MongoDB Atlas`

The initial universe catalog is deliberately kept in `CatalogService` so the app remains usable when MongoDB is offline. Conversations use MongoDB when configured. This means the selection experience has no database dependency, while saved conversations use the configured Atlas database.

## Project folders

```text
anipersona-backend/     Java Spring Boot API
frontend/               React/Vite application
leo/                    Existing unrelated project, preserved unchanged
```

## Prerequisites

- Java 17+ and Maven
- Node.js 20+
- MongoDB Atlas account for persistence
- Ollama installed locally for live conversations

## Configure on Windows PowerShell

Open PowerShell in `anipersona-backend`. Set the variables for the current terminal only; never put real secrets in source code.

```powershell
$env:MONGODB_URI="mongodb+srv://USERNAME:PASSWORD@CLUSTER/anipersona?retryWrites=true&w=majority"
$env:OLLAMA_MODEL="llama3.2"
$env:FRONTEND_ORIGIN="http://localhost:5173"
mvn spring-boot:run
```

`anipersona-backend/.env.example` is a safe reference, not an automatically loaded file. Spring Boot reads the environment variables set in the terminal that launches it. MongoDB Atlas must also allow your current IP address in Network Access.

If Maven tries to create `C:\.m2` and Windows denies access, use the included settings file:

```powershell
mvn -s maven-local-settings.xml spring-boot:run
```

In a second PowerShell window, open `frontend`, create `.env` from `.env.example` if the backend is not on the default address, then run:

```powershell
npm install
npm run dev
```

Open the URL Vite prints (normally `http://localhost:5173`).

Before launching the backend, install Ollama from [ollama.com](https://ollama.com), then use a PowerShell or CMD terminal to download the model once:

```cmd
ollama pull llama3.2
```

Ollama normally starts its local service automatically. If it does not, run `ollama serve` in a separate terminal.

## API

| Method | Endpoint | Purpose |
| --- | --- | --- |
| GET | `/` | Backend welcome message |
| GET | `/api/health` | Health response |
| GET | `/api/anime/themes` | Universe catalog |
| GET | `/api/anime/{id}/characters` | Characters in a universe |
| GET | `/api/characters/{id}` | Character metadata |
| POST | `/api/chat` | Generate a persona response through Groq |
| GET/POST | `/api/conversations` | List/create anonymous-session conversations |
| GET/DELETE | `/api/conversations/{id}` | Read/delete a session-owned conversation |

Use Postman with `GET http://localhost:8081/api/health` first. For chat, send `Content-Type: application/json` and a body like:

```json
{
  "universeId": "doraemon",
  "aiCharacterId": "doraemon",
  "userCharacterId": "nobita",
  "scenario": "Nobita is worried about an exam.",
  "tone": "Friendly",
  "language": "English",
  "messages": [{ "role": "user", "content": "Doraemon, I am worried about my exam." }]
}
```

## Common issues

- **Ollama is not running:** install Ollama, run `ollama pull llama3.2`, then open or restart Ollama.
- **Conversation storage unavailable:** verify the Atlas URI, database user, and Atlas Network Access allow-list. The catalog and chat do not require MongoDB.
- **Frontend cannot reach backend:** ensure backend runs on `8081`, frontend on `5173`, and `FRONTEND_ORIGIN` matches the frontend URL.

## Current security scope

Chat requests have bounded message histories and server-built instructions, so clients cannot submit a system prompt. Saved conversations are isolated by a generated anonymous session header; this is suitable for a demo, not full production authentication. A future phase should add users and authenticated ownership before public deployment.

## Future enhancements

Add sign-in, persistent catalog administration, streaming responses, moderation, image assets, and formal test coverage for controllers and services.
