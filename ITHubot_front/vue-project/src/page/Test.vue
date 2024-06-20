<script setup>
import { onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';
import Header from '@/components/Header.vue';
import api from '@/api.js';

const test = ref(null);
const questions = ref([]);
const route = useRoute(); 

const newQuestion = ref({
  test: {
    testId: route.params.id
  },
  content: '',
});

const newAnswer = ref({
  question: null, 
  content: '',
  isCorrect: false,
});

const fetchTestDetail = async () => {
  const id = route.params.id;
  try {
    const response = await api.get(`/admin/get/test/${id}`, {
      headers: {
        'Authorization': 'Bearer ' + $cookies.get('jwt')
      }
    });
    test.value = response.data;
  } catch (error) {
    console.error(error);
  }
};

const fetchQuestionDetail = async () => {
  const id = route.params.id;
  try {
    const response = await api.get(`/admin/questions/${id}`, {
      headers: {
        'Authorization': 'Bearer ' + $cookies.get('jwt')
      }
    });
    questions.value = response.data;
    await Promise.all(questions.value.map(async (question) => {
      question.answers = await fetchAnswersForQuestion(question.questionId);
    }));
  } catch (error) {
    console.error(error);
  }
};

const fetchAnswersForQuestion = async (questionId) => {
  try {
    const response = await api.get(`/admin/get/question/answer/${questionId}`, {
      headers: {
        'Authorization': 'Bearer ' + $cookies.get('jwt')
      }
    });
    return response.data;
  } catch (error) {
    console.error(error);
    return [];
  }
};

const updateTestDetail = async () => {
  const id = route.params.id;
  try {
    await api.post(`/admin/update/test/${id}`, test.value, {
      headers: {
        'Authorization': 'Bearer ' + $cookies.get('jwt')
      }
    });
    window.location.reload();
  } catch (error) {
    console.error(error);
  }
};

const updateQuestionDetail = async (question) => {
  try {
    await api.post(`/admin/update/question/${question.questionId}`, question, {
      headers: {
        'Authorization': 'Bearer ' + $cookies.get('jwt')
      }
    });
    window.location.reload();
  } catch (error) {
    console.error(error);
  }
};

const addQuestion = async () => {
  try {
    await api.post(`/admin/create/question`, newQuestion.value, {
      headers: {
        'Authorization': 'Bearer ' + $cookies.get('jwt')
      }
    });
    newQuestion.value = { test: { testId: route.params.id }, content: '' };
    fetchQuestionDetail();
  } catch (error) {
    console.error(error);
  }
};

const addAnswer = async (questionId) => {
  const question = questions.value.find(q => q.questionId === questionId);
  newAnswer.value.question = { questionId: question.questionId }; // Устанавливаем только ID вопроса
  try {
    await api.post(`/admin/create/answer`, newAnswer.value, {
      headers: {
        'Authorization': 'Bearer ' + $cookies.get('jwt')
      }
    });
    newAnswer.value = { question: null, content: '', isCorrect: false };
    question.answers = await fetchAnswersForQuestion(questionId); // Reload answers for the current question
  } catch (error) {
    console.error(error);
  }
};

onMounted(() => {
  fetchTestDetail();
  fetchQuestionDetail();
});
</script>


<template>
  <div>
    <Header />
    <div class="container" v-if="test">
      <h1>Редактирование теста</h1>
      <form @submit.prevent="updateTestDetail">
        <div>
          <label for="title">Название теста</label>
          <input id="title" v-model="test.title" type="text" required />
        </div>
        <div>
          <label for="description">Описание теста</label>
          <textarea id="description" v-model="test.description" required></textarea>
        </div>
        <button type="submit">Сохранить изменения</button>
      </form>

      <div class="card-container">
        <div class="card" v-for="question in questions" :key="question.questionId">
          <form @submit.prevent="updateQuestionDetail(question)">
            <div class="card-body">
              <label for="content">Вопрос</label>
              <input id="content" v-model="question.content" type="text" required />
              
              <h4>Ответы на вопрос</h4>
              <ul>
                <li v-for="answer in question.answers" :key="answer.answerId">{{ answer.content }}</li>
              </ul>
              
              <h5>Добавить новый ответ</h5>
              <form @submit.prevent="addAnswer(question.questionId)">
                <div>
                  <label for="new-answer">Новый ответ</label>
                  <input id="new-answer" v-model="newAnswer.content" type="text" required />
                </div>
                <div>
                  <label>
                    <input type="checkbox" v-model="newAnswer.isCorrect" />
                    Правильный ответ
                  </label>
                </div>
                <button type="submit">Добавить ответ</button>
              </form>
              
              <button type="submit">Сохранить изменения</button>
            </div>
          </form>
        </div>
      </div>

      <h3>Добавить новый вопрос</h3>
      <form @submit.prevent="addQuestion">
        <div>
          <label for="new-content">Новый вопрос</label>
          <input id="new-content" v-model="newQuestion.content" type="text" required />
        </div>
        <button type="submit">Добавить вопрос</button>
      </form>
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

form {
  margin-bottom: 20px;
}

label {
  display: block;
  margin-bottom: 5px;
}

input, textarea {
  width: 100%;
  padding: 8px;
  margin-bottom: 10px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

button {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
}

button:hover {
  background-color: #0056b3;
}

.card-container {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.card {
  background-color: #f9f9f9;
  border: 1px solid #ccc;
  border-radius: 4px;
  padding: 20px;
  width: 100%;
}

.card-body {
  display: flex;
  flex-direction: column;
}
</style>
