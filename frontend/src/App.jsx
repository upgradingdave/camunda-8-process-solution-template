import { useState, useEffect } from 'react';

const styles = {
  page: { fontFamily: 'sans-serif', maxWidth: 720, margin: '40px auto', padding: '0 20px' },
  header: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 24 },
  title: { margin: 0, fontSize: 24 },
  userInfo: { display: 'flex', alignItems: 'center', gap: 12 },
  username: { color: '#555', fontSize: 14 },
  logoutBtn: { padding: '6px 14px', cursor: 'pointer', borderRadius: 4, border: '1px solid #ccc' },
  emptyMsg: { color: '#888', fontSize: 16, marginTop: 32 },
  taskCard: {
    border: '1px solid #e0e0e0',
    borderRadius: 8,
    padding: '16px 20px',
    marginBottom: 12,
    background: '#fafafa',
  },
  taskName: { margin: '0 0 8px', fontSize: 16 },
  taskMeta: { display: 'flex', gap: 20, fontSize: 13, color: '#666' },
  taskKey: { marginTop: 6, fontSize: 11, color: '#aaa' },
  error: { color: '#c0392b', marginTop: 32 },
};

export default function App({ keycloak }) {
  const [tasks, setTasks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const username = keycloak.tokenParsed?.preferred_username;

  useEffect(() => {
    keycloak
      .updateToken(30)
      .then(() =>
        fetch('/api/camunda/userTasks', {
          headers: { Authorization: `Bearer ${keycloak.token}` },
        })
      )
      .then(res => {
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        return res.json();
      })
      .then(data => {
        setTasks(data);
        setLoading(false);
      })
      .catch(err => {
        setError(err.message);
        setLoading(false);
      });
  }, [keycloak]);

  if (loading) return <div style={styles.page}><p>Loading tasks...</p></div>;

  return (
    <div style={styles.page}>
      <div style={styles.header}>
        <h1 style={styles.title}>My Tasks</h1>
        <div style={styles.userInfo}>
          <span style={styles.username}>{username}</span>
          <button style={styles.logoutBtn} onClick={() => keycloak.logout()}>Logout</button>
        </div>
      </div>

      {error && <p style={styles.error}>Error loading tasks: {error}</p>}

      {!error && tasks.length === 0 && (
        <p style={styles.emptyMsg}>All caught up, you have no tasks assigned.</p>
      )}

      {tasks.map(task => (
        <div key={task.userTaskKey} style={styles.taskCard}>
          <h3 style={styles.taskName}>{task.name || task.elementId}</h3>
          <div style={styles.taskMeta}>
            <span>Process: {task.bpmnProcessId}</span>
            {task.dueDate && (
              <span>Due: {new Date(task.dueDate).toLocaleDateString()}</span>
            )}
            {task.priority != null && <span>Priority: {task.priority}</span>}
          </div>
          <div style={styles.taskKey}>Key: {task.userTaskKey}</div>
        </div>
      ))}
    </div>
  );
}
