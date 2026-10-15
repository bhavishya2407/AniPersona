const BASE = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081/api';
const sessionKey='anipersona-session';
export const sessionId=()=>{ let id=localStorage.getItem(sessionKey); if(!id){id=crypto.randomUUID().replaceAll('-','');localStorage.setItem(sessionKey,id)} return id };
export async function api(path, options={}) { const r=await fetch(`${BASE}${path}`,{headers:{'Content-Type':'application/json','X-Session-Id':sessionId(),...(options.headers||{})},...options}); if(!r.ok){let data;try{data=await r.json()}catch{} throw new Error(data?.message||'Unable to complete that request.')} return r.status===204?null:r.json(); }
export const getUniverses=()=>api('/anime/themes');
export const getCharacters=(id)=>api(`/anime/${id}/characters`);
export const askCharacter=(payload)=>api('/chat',{method:'POST',body:JSON.stringify(payload)});
