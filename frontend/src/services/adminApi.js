import client from '../api/client';

export const fetchUsers = async () => {
  const response = await client.get('/api/admin/users');
  return response.data.data.content;
};

export const fetchWallets = async () => {
  const response = await client.get('/api/admin/wallets');
  return response.data.data.content;
};

export const fetchTransactions = async () => {
  const response = await client.get('/api/admin/transactions');
  return response.data.data.content;
};

export const freezeWallet = async (walletId) => {
  await client.put(`/api/admin/wallets/${walletId}/freeze`);
};

export const unfreezeWallet = async (walletId) => {
  await client.put(`/api/admin/wallets/${walletId}/unfreeze`);
};
