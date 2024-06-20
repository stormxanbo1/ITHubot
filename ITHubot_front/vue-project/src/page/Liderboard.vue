<template>
    <div>
      <Header />
      <div class="container">
        <h1>Список Лидеров</h1>
        <table>
          <thead>
            <tr>
              <th @click="sortUsers('username')">Имя пользователя</th>
              <th @click="sortUsers('totalScore')">Баллы</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="user in sortedUsers" :key="user.userId">
              <td>{{ user.username }}</td>
              <td :class="{ 'green-text': user.totalScore > 100 }">{{ user.totalScore }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, computed, onMounted } from 'vue';
  import Header from '@/components/Header.vue';
  import api from '@/api.js';
  
  const users = ref([]);
  const sortKey = ref('totalScore');
  const sortOrder = ref('desc');
  
  const fetchUsers = async () => {
    try {
      const response = await api.get('/main/get/users', {
        headers: {
          'Authorization': 'Bearer ' + $cookies.get('jwt')
        }
      });
  
      // Load user scores
      for (const user of response.data) {
        const userScoreResponse = await api.get(`/admin/score/${user.userId}`, {
          headers: {
            'Authorization': 'Bearer ' + $cookies.get('jwt')
          }
        });
        user.totalScore = userScoreResponse.data.totalScore;
      }
  
      users.value = response.data;
    } catch (error) {
      console.error(error);
    }
  };
  
  const sortUsers = (key) => {
    sortKey.value = key;
    sortOrder.value = sortOrder.value === 'asc' ? 'desc' : 'asc';
  
    users.value.sort((a, b) => {
      let aValue = key === 'totalScore' ? a.totalScore : a[key];
      let bValue = key === 'totalScore' ? b.totalScore : b[key];
  
      let result = aValue < bValue ? -1 : (aValue > bValue ? 1 : 0);
      return sortOrder.value === 'asc' ? result : -result;
    });
  };
  
  const sortedUsers = computed(() => {
    return [...users.value].sort((a, b) => {
      let aValue = sortKey.value === 'totalScore' ? a.totalScore : a[sortKey.value];
      let bValue = sortKey.value === 'totalScore' ? b.totalScore : b[sortKey.value];
  
      let result = aValue < bValue ? -1 : (aValue > bValue ? 1 : 0);
      return sortOrder.value === 'asc' ? result : -result;
    });
  });
  
  onMounted(fetchUsers);
  </script>
  
  <style scoped>
  .container {
    max-width: 900px;
    margin: 20px auto;
    padding: 20px;
    background-color: #f0f0f0;
    border-radius: 10px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  }
  
  h1 {
    text-align: center;
    color: #333;
    font-size: 2.5rem;
    margin-bottom: 20px;
  }
  
  table {
    width: 100%;
    border-collapse: collapse;
    margin-top: 20px;
    background-color: #fff;
    border-radius: 10px;
    overflow: hidden;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  }
  
  th, td {
    padding: 15px;
    text-align: left;
    border-bottom: 1px solid #ddd;
  }
  
  th {
    cursor: pointer;
    background-color: #f5f5f5;
    color: #333;
    font-weight: bold;
  }
  
  th:hover {
    background-color: #e0e0e0;
  }
  
  tr:nth-child(even) {
    background-color: #f9f9f9;
  }
  
  .green-text {
    color: green;
    font-weight: bold;
  }
  
  @media (max-width: 768px) {
    .container {
      padding: 15px;
    }
  
    h1 {
      font-size: 2rem;
    }
  
    table {
      font-size: 0.9rem;
    }
  
    th, td {
      padding: 10px;
    }
  }
  </style>
  