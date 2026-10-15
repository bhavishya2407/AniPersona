
import { createContext, useContext, useEffect, useState } from 'react';

const Context = createContext();

function getSavedState() {
    try {
        return JSON.parse(localStorage.getItem('anipersona-roleplay') || '{}');
    } catch {
        return {};
    }
}

export function RoleplayProvider({ children }) {
    const [state, setState] = useState(getSavedState);

    useEffect(() => {
        localStorage.setItem('anipersona-roleplay', JSON.stringify(state));
    }, [state]);

    const clear = () => setState({});

    return (
        <Context.Provider value={{ state, setState, clear }}>
            {children}
        </Context.Provider>
    );
}

export const useRoleplay = () => useContext(Context);

