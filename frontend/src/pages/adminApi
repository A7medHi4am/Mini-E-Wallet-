import axios from 'axios';

const apiClient = axios.create({
  baseURL: '/api/admin',
  headers: {
    'Content-Type': 'application/json',
  },
});

export const fetchUsers = async () => {
  const response = await apiClient.get('/users');
  // Adjust based on your backend response structure
  return response.data.data.content;
};

export const fetchWallets = async () => {
  const response = await apiClient.get('/wallets');
  return response.data.data.content;
};

export const fetchTransactions = async () => {
  const response = await apiClient.get('/transactions');
  return response.data.data.content;
};

export const freezeWallet = async (walletId) => {
  await apiClient.put(`/wallets/${walletId}/freeze`);
};

export const unfreezeWallet = async (walletId) => {
  await apiClient.put(`/wallets/${walletId}/unfreeze`);
};
