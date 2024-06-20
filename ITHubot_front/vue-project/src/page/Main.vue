<template>
  <div class="main-container">
    <Header />
    <h1>Список тестов</h1>
    <button class="add-button" @click="TestCreater">Добавить новый тест</button>
    
    <div class="card-container">
      <div class="card" v-for="test in tests" :key="test.testId">
        <div class="card-body">
          <h5 class="card-title">{{ test.title }}</h5>
          <p class="card-text">{{ test.description }}</p>
          <a :href="`/test/${test.testId}`" class="btn btn-primary">Редактировать</a>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';
import Header from '@/components/Header.vue';
import api from '@/api.js';

const tests = ref(null);
const route = useRoute();

const TestCreater = async () => {
  const TestData = {
    title: "Test Name",
    description: "Description Your Test"
  };
  try {
    const response = await api.post('/admin/create/test', TestData, {
      headers: {
        'Authorization': 'Bearer ' + $cookies.get('jwt')
      }
    });
    console.log('тест успешно создан');
    window.location.reload();
  } catch (error) {
    console.error('Ошибка при создании теста:', error);
  }
};

const fetchTestsDetail = async () => {
  try {
    const response = await api.get('/admin/get/test', {
      headers: {
        'Authorization': 'Bearer ' + $cookies.get('jwt')
      }
    });
    tests.value = response.data;
    console.log(response.data);
  } catch (error) {
    console.error('Ошибка при получении данных:', error);
  }
};

onMounted(fetchTestsDetail);
</script>

<style scoped>
.main-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding: 20px;
  background: linear-gradient(135deg, #ffafbd, #ffc3a0);
  font-family: 'Helvetica Neue', sans-serif;
  min-width: 70%;
}

h1 {
  font-size: 36px;
  color: #4b0082;
  margin: 20px 0;
}

.add-button {
  margin: 20px 0;
  padding: 10px 20px;
  font-size: 18px;
  color: #fff;
  background-color: #4b0082;
  border: none;
  border-radius: 30px;
  cursor: pointer;
  transition: background-color 0.3s, transform 0.3s;
}

.add-button:hover {
  background-color: #6a0dad;
  transform: translateY(-2px);
}

.card-container {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
}

.card {
  background: white;
  border-radius: 10px;
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.3);
  width: 18rem;
  margin: 1rem;
  overflow: hidden;
  transition: transform 0.3s, box-shadow 0.3s;
}

.card:hover {
  transform: translateY(-10px);
  box-shadow: 0 16px 32px rgba(0, 0, 0, 0.3);
}

.card-body {
  padding: 15px;
}

.card-title {
  font-size: 20px;
  margin-bottom: 10px;
}

.card-text {
  font-size: 16px;
  color: #555;
}

.btn {
  color: aliceblue;
  background-color: rgb(35, 25, 66);
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
  transition: background 0.3s;
}

.btn:hover {
  background-color: rgb(55, 35, 86);
}

@media (max-width: 768px) {
  .card-container {
    flex-direction: column;
    align-items: center;
  }
}
</style>
