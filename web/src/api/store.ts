let store: string | null = null;

export const setToken = (t: string | null) => {
  store = t;
};

export const getToken = () => store;
