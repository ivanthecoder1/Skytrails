class User {
  constructor(
    public username: string,
    public firstname: string,
    public lastname: string
  ) {}
}

class SessionManager {
  private static instance: SessionManager;
  private sessionKey: string | null = null;
  private user: User | null = null;

  private constructor() {}

  static getInstance(): SessionManager {
    if (!SessionManager.instance) {
      SessionManager.instance = new SessionManager();
    }

    return SessionManager.instance;
  }

  setSessionKey(key: string): void {
    this.sessionKey = key;
  }

  getSessionKey(): string | null {
    return this.sessionKey;
  }

  setUser(user: User): void {
    this.user = user;
  }

  getUser(): User | null {
    return this.user;
  }
}

export default SessionManager;
