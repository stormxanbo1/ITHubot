<template>
    <div class="main-container">
      <h1>Вы авторизовались</h1>
      <h2>Добро пожаловать в нашего великолепного бота!</h2>
      <h1>Список тренеров</h1>
      <div class="card-container">
        <div class="card" v-for="trainer in training" :key="trainer.id">
          <img :src="trainer.user.img" alt="trainer-avatar" class="card-img-top" />
          <div class="card-body">
            <h5 class="card-title">{{ trainer.user.name }}</h5>
            <p class="card-text">{{ trainer.specialization }}</p>
            <!-- <a :href="`/trainer/${trainer.id}`" class="btn btn-primary">Подробнее</a> -->
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { onMounted, ref } from 'vue';
  import { useRoute } from 'vue-router';
  import api from '@/api.js';
  
  const training = ref(null);
  const route = useRoute();
  
  const fetchTrainingDetail = async () => {
    try {
      const response = await api.get('/unauthorized/get/coach/', {
        headers: {
          'Authorization': 'Bearer ' + $cookies.get('jwt')
        }
      });
      console.log(response.data);
      training.value = response.data;
    } catch (error) {
      console.error(error);
    }
  };
  
  onMounted(fetchTrainingDetail);
  </script>
  
  <style scoped>
  .main-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    text-align: center;
    padding: 20px;
    background: linear-gradient(135deg, #ffafbd, #ffc3a0);
    font-family: 'Helvetica Neue', sans-serif;
  }
  
  h1 {
    font-size: 36px;
    color: #4b0082;
    margin: 20px 0;
  }
  
  h2 {
    font-size: 24px;
    color: #4b0082;
    margin-bottom: 30px;
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
  
  .card-img-top {
    width: 100%;
    height: 15rem;
    object-fit: cover;
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
  