<template>
    <div>
      <Header />
      <div class="container">
        <h1>Список пользователей</h1>
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
              <td>{{ user.totalScore }}</td>
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
    console.log(response.data)
    // После получения пользователей, загрузите их баллы через API вызов
    for (const user of response.data) {
      const userScoreResponse = await api.get(`/admin/${user.userId}/score`, {
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
    let aValue, bValue;
    if (key === 'totalScore') {
      aValue = a.totalScore;
      bValue = b.totalScore;
    } else {
      // Дополнительные условия для других полей, если требуется
      aValue = a[key];
      bValue = b[key];
    }

    let result = 0;
    if (aValue < bValue) {
      result = -1;
    } else if (aValue > bValue) {
      result = 1;
    }
    return sortOrder.value === 'asc' ? result : -result;
  });
};

  
  const sortedUsers = computed(() => {
    return [...users.value].sort((a, b) => {
      let result = 0;
      if (a[sortKey.value] < b[sortKey.value]) {
        result = -1;
      } else if (a[sortKey.value] > b[sortKey.value]) {
        result = 1;
      }
      return sortOrder.value === 'asc' ? result : -result;
    });
  });
  
  onMounted(fetchUsers);
  </script>
  
  <style scoped>
  .container {
    max-width: 800px;
    margin: 20px auto;
    padding: 20px;
    background-color: #fff;
    border-radius: 5px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  }
  
  h1 {
    text-align: center;
    color: #333;
  }
  
  table {
    width: 100%;
    border-collapse: collapse;
    margin-top: 20px;
  }
  
  th, td {
    padding: 12px;
    text-align: left;
    border-bottom: 1px solid #ddd;
  }
  
  th {
    cursor: pointer;
  }
  
  th:hover {
    background-color: #f1f1f1;
  }
  </style>
  