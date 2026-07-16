import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import client from '../api/client';
import { clearToken } from '../auth/auth';
import {
  fetchUsers,
  fetchWallets,
  fetchTransactions,
  freezeWallet,
  unfreezeWallet,
  fetchMerchants,
  createMerchant,
} from '../services/adminApi';

const AdminDashboard = () => {
  const navigate = useNavigate();
  const [profile, setProfile] = useState(null);
  const [users, setUsers] = useState([]);
  const [wallets, setWallets] = useState([]);
  const [transactions, setTransactions] = useState([]);
  const [merchants, setMerchants] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [refreshFlag, setRefreshFlag] = useState(false);

  const [merchantName, setMerchantName] = useState('');
  const [merchantCategory, setMerchantCategory] = useState('');
  const [merchantError, setMerchantError] = useState('');
  const [creatingMerchant, setCreatingMerchant] = useState(false);

  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      setError(null);
      try {
        const [profileRes, usersRes, walletsRes, transactionsRes, merchantsRes] = await Promise.all([
          client.get('/api/auth/user'),
          fetchUsers(),
          fetchWallets(),
          fetchTransactions(),
          fetchMerchants(),
        ]);

        setProfile(profileRes.data.data);
        setUsers(usersRes);
        setWallets(walletsRes);
        setTransactions(transactionsRes);
        setMerchants(merchantsRes);
      } catch (err) {
        setError('Failed to fetch admin data.');
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [refreshFlag]);

  const handleCreateMerchant = async (event) => {
    event.preventDefault();
    if (!merchantName.trim() || !merchantCategory.trim()) {
      setMerchantError('Name and category are required.');
      return;
    }

    setMerchantError('');
    setCreatingMerchant(true);
    try {
      await createMerchant({ name: merchantName.trim(), category: merchantCategory.trim() });
      setMerchantName('');
      setMerchantCategory('');
      setRefreshFlag(!refreshFlag);
    } catch (err) {
      setMerchantError(err.response?.data?.error || 'Failed to create merchant.');
    } finally {
      setCreatingMerchant(false);
    }
  };

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

  const handleLogout = () => {
    clearToken();
    navigate('/login');
  };

  if (loading) return <div className="card">Loading...</div>;
  if (error) return <div className="card error">{error}</div>;

  return (
    <div className="card card-wide admin-dashboard">
      <div className="admin-header">
        <div>
          <h1>Admin dashboard</h1>
          <p className="subtitle">Review account activity, wallet health, and audit controls.</p>
          {profile && (
            <div className="admin-profile-summary">
              <p>
                Signed in as <strong>{profile.name}</strong> — <span>{profile.role}</span>
              </p>
              <p className="muted-inline">{profile.email}</p>
            </div>
          )}
        </div>

        <div className="admin-actions">
          <Link to="/profile" className="button-link secondary">
            My profile
          </Link>
          <button type="button" className="button-link secondary" onClick={handleLogout}>
            Log out
          </button>
        </div>
      </div>

      <div className="admin-summary">
        <div className="admin-summary-card">
          <span className="summary-label">Users</span>
          <strong>{users.length}</strong>
        </div>
        <div className="admin-summary-card">
          <span className="summary-label">Wallets</span>
          <strong>{wallets.length}</strong>
        </div>
        <div className="admin-summary-card">
          <span className="summary-label">Transactions</span>
          <strong>{transactions.length}</strong>
        </div>
        <div className="admin-summary-card">
          <span className="summary-label">Merchants</span>
          <strong>{merchants.length}</strong>
        </div>
      </div>

      <section className="admin-section">
        <div className="section-heading">
          <h2>Users</h2>
          <p className="hint">All registered users in the system.</p>
        </div>
        <table className="admin-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Name</th>
              <th>Email</th>
            </tr>
          </thead>
          <tbody>
            {users.length === 0 ? (
              <tr>
                <td colSpan="3" className="empty-state">
                  No users available.
                </td>
              </tr>
            ) : (
              users.map((user) => (
                <tr key={user.id}>
                  <td>{user.id}</td>
                  <td>{user.name}</td>
                  <td>{user.email}</td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </section>

      <section className="admin-section">
        <div className="section-heading">
          <h2>Wallets</h2>
          <p className="hint">Freeze or unfreeze wallets as needed.</p>
        </div>
        <table className="admin-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>User</th>
              <th>Balance</th>
              <th>Status</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            {wallets.length === 0 ? (
              <tr>
                <td colSpan="5" className="empty-state">
                  No wallet records found.
                </td>
              </tr>
            ) : (
              wallets.map((wallet) => (
                <tr key={wallet.id}>
                  <td>{wallet.id}</td>
                  <td>{wallet.ownerName}</td>
                  <td>{Number(wallet.balance).toFixed(2)}</td>
                  <td>
                    <span className={`badge ${wallet.status.toLowerCase()}`}>
                      {wallet.status}
                    </span>
                  </td>
                  <td>
                    {wallet.status === 'ACTIVE' ? (
                      <button onClick={() => handleFreeze(wallet.id)}>
                        Freeze
                      </button>
                    ) : (
                      <button onClick={() => handleUnfreeze(wallet.id)}>
                        Unfreeze
                      </button>
                    )}
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </section>

      <section className="admin-section">
        <div className="section-heading">
          <h2>Merchants</h2>
          <p className="hint">Create a merchant so users can pay it from their wallet.</p>
        </div>

        <form className="merchant-form" onSubmit={handleCreateMerchant}>
          <label>
            Name
            <input
              value={merchantName}
              onChange={(e) => setMerchantName(e.target.value)}
              placeholder="Coffee Shop"
            />
          </label>
          <label>
            Category
            <input
              value={merchantCategory}
              onChange={(e) => setMerchantCategory(e.target.value)}
              placeholder="Food & Drink"
            />
          </label>
          <button type="submit" disabled={creatingMerchant}>
            {creatingMerchant ? 'Creating…' : 'Create merchant'}
          </button>
        </form>

        {merchantError && (
          <div className="error" role="alert">
            {merchantError}
          </div>
        )}

        <table className="admin-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Name</th>
              <th>Category</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            {merchants.length === 0 ? (
              <tr>
                <td colSpan="4" className="empty-state">
                  No merchants yet.
                </td>
              </tr>
            ) : (
              merchants.map((merchant) => (
                <tr key={merchant.id}>
                  <td>{merchant.id}</td>
                  <td>{merchant.name}</td>
                  <td>{merchant.category}</td>
                  <td>
                    <span className={`badge ${merchant.active ? 'active' : 'inactive'}`}>
                      {merchant.active ? 'ACTIVE' : 'INACTIVE'}
                    </span>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </section>

      <section className="admin-section">
        <div className="section-heading">
          <h2>Recent transactions</h2>
          <p className="hint">Latest activity across the wallet platform.</p>
        </div>
        <table className="admin-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>From wallet</th>
              <th>To wallet</th>
              <th>Amount</th>
              <th>Type</th>
              <th>Status</th>
              <th>Date</th>
            </tr>
          </thead>
          <tbody>
            {transactions.length === 0 ? (
              <tr>
                <td colSpan="7" className="empty-state">
                  No transactions yet.
                </td>
              </tr>
            ) : (
              transactions.map((tx) => (
                <tr key={tx.id}>
                  <td>{tx.id}</td>
                  <td>{tx.senderWalletId ?? "—"}</td>
                  <td>{tx.receiverWalletId ?? "—"}</td>
                  <td>{Number(tx.amount).toFixed(2)}</td>
                  <td>{tx.type}</td>
                  <td>{tx.status}</td>
                  <td>{new Date(tx.createdAt).toLocaleString()}</td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </section>
    </div>
  );
};

export default AdminDashboard;
