

<script setup>
import { onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';
import Header from '@/components/Heder.vue';
import api from '@/api.js';

const test = ref(null);
const route = useRoute();

const fetchTestDetail = async () => {
  const id = route.params.id;
  try {
    const response = await api.get(`/admin/get/test/${id}`, {
      headers: {
        'Authorization': 'Bearer ' + $cookies.get('jwt')
      }
    });
    console.log(response.data);
    test.value = response.data;
    
  } catch (error) {
    console.error(error);
  }
};


onMounted(fetchTestDetail);




</script>

  

<template>
  <div>
    <Header />
    <div class="container" v-if="test">
    
      <h1>{{ test.title }}</h1>
      <p>{{ test.description }}</p>
      <h3>Упражнения</h3>
      <ul>
        <!-- <li v-for="exercise in training.exercises" :key="exercise.id">{{ exercise.name }}</li> -->
      </ul>
    </div>
  </div>
</template>


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
  
  ul {
    list-style-type: none;
    padding: 0;
  }
  
  li {
    background-color: #f9f9f9;
    margin: 5px 0;
    padding: 10px;
    border-left: 6px solid #007bff;
  }
  </style>