import React, { useEffect, useState } from 'react';
import { fetchUsers, fetchWallets, fetchTransactions, freezeWallet, unfreezeWallet } from '../services/adminApi';

const AdminDashboard = () => {
  const [users, setUsers] = useState([]);
  const [wallets, setWallets] = useState([]);
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [refreshFlag, setRefreshFlag] = useState(false);

  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      setError(null);
      try {
        const [usersRes, walletsRes, transactionsRes] = await Promise.all([
          fetchUsers(),
          fetchWallets(),
          fetchTransactions(),
        ]);
        setUsers(usersRes);
        setWallets(walletsRes);
        setTransactions(transactionsRes);
      } catch (err) {
        setError('Failed to fetch data.');
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, [refreshFlag]);

  const handleFreeze = async (walletId) => {
    try {
      await freezeWallet(walletId);
      setRefreshFlag(!refreshFlag);
    } catch {
      alert('Failed to freeze wallet.');
    }
  };

  const handleUnfreeze = async (walletId) => {
    try {
      await unfreezeWallet(walletId);
      setRefreshFlag(!refreshFlag);
    } catch {
      alert('Failed to unfreeze wallet.');
    }
  };

  if (loading) return <div>Loading...</div>;
  if (error) return <div>Error: {error}</div>;

  return (
    <div className="admin-dashboard">
      <h1>Admin Dashboard</h1>

      {/* Users Table */}
      <section>
        <h2>Users</h2>
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Name</th>
              <th>Email</th>
            </tr>
          </thead>
          <tbody>
            {users.map((user) => (
              <tr key={user.id}>
                <td>{user.id}</td>
                <td>{user.name}</td>
                <td>{user.email}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </section>

      {/* Wallets Table */}
      <section>
        <h2>Wallets</h2>
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>User</th>
              <th>Balance</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {wallets.map((wallet) => (
              <tr key={wallet.id}>
                <td>{wallet.id}</td>
                <td>{wallet.ownerName}</td>
                <td>{wallet.balance}</td>
                <td>{wallet.status}</td>
                <td>
                  {wallet.status === 'ACTIVE' ? (
                    <button onClick={() => handleFreeze(wallet.id)}>Freeze</button>
                  ) : (
                    <button onClick={() => handleUnfreeze(wallet.id)}>Unfreeze</button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </section>

      {/* Transactions Table */}
      <section>
        <h2>Transactions</h2>
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>User</th>
              <th>Amount</th>
              <th>Type</th>
              <th>Date</th>
            </tr>
          </thead>
          <tbody>
            {transactions.map((tx) => (
              <tr key={tx.id}>
                <td>{tx.id}</td>
                <td>{tx.userName}</td>
                <td>{tx.amount}</td>
                <td>{tx.type}</td>
                <td>{new Date(tx.date).toLocaleString()}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </section>
    </div>
  );
};

export default AdminDashboard;
