<template>
  <div>
    <Header />
    <div class="container" v-if="test">
      <h1>Редактирование теста</h1>
      <form @submit.prevent="updateTestDetail" class="form-section">
        <div class="form-group">
          <label for="title">Название теста</label>
          <input id="title" v-model="test.title" type="text" required />
        </div>
        <div class="form-group">
          <label for="description">Описание теста</label>
          <textarea id="description" v-model="test.description" required></textarea>
        </div>
        <button type="submit" class="btn-primary">Сохранить изменения</button>
      </form>

      <div class="card-container">
        <div class="card" v-for="question in questions" :key="question.questionId">
          <form @submit.prevent="updateQuestionDetail(question)" class="form-section">
            <div class="card-body">
              <div class="form-group">
                <label for="content">Вопрос</label>
                <input id="content" v-model="question.content" type="text" required />
              </div>
              <h4>Ответы на вопрос</h4>
              <ul>
                <li v-for="answer in question.answers" :key="answer.answerId">{{ answer.content }}</li>
              </ul>

              <h5>Добавить новый ответ</h5>
              <form @submit.prevent="addAnswer(question.questionId)" class="form-section">
                <div class="form-group">
                  <label for="new-answer">Новый ответ</label>
                  <input id="new-answer" v-model="newAnswer.content" type="text" required />
                </div>
                <div class="form-group">
                  <label>
                    <input type="checkbox" v-model="newAnswer.isCorrect" />
                    Правильный ответ
                  </label>
                </div>
                <button type="submit" class="btn-secondary">Добавить ответ</button>
              </form>

              <button type="submit" class="btn-primary">Сохранить изменения</button>
            </div>
          </form>
        </div>
      </div>

      <h3>Добавить новый вопрос</h3>
      <form @submit.prevent="addQuestion" class="form-section">
        <div class="form-group">
          <label for="new-content">Новый вопрос</label>
          <input id="new-content" v-model="newQuestion.content" type="text" required />
        </div>
        <button type="submit" class="btn-secondary">Добавить вопрос</button>
      </form>
    </div>
  </div>
</template>

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
    const response = await api.get(`/main/get/test/${id}`, {
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
    const response = await api.get(`/main/questions/${id}`, {
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
    const response = await api.get(`/main/get/question/answer/${questionId}`, {
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

<style scoped>
.container {
  max-width: 900px;
  margin: 20px auto;
  padding: 40px;
  background-color: #f9f9f9;
  border-radius: 16px;
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

h1, h3 {
  text-align: center;
  color: #333;
  margin-bottom: 20px;
  font-family: 'Roboto', sans-serif;
}

h1 {
  font-size: 42px; /* Увеличенный размер шрифта */
  font-weight: 700;
}

h3 {
  font-size: 32px; /* Увеличенный размер шрифта */
  font-weight: 500;
}

ul {
  list-style-type: none;
  padding: 0;
}

li {
  background-color: #ffffff;
  margin: 10px 0;
  padding: 15px;
  border-left: 4px solid #0088cc;
  border-radius: 8px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

.form-section {
  margin-bottom: 30px;
}

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;
  margin-bottom: 8px;
  font-weight: bold;
  color: #555;
  font-family: 'Roboto', sans-serif;
  font-size: 18px; /* Увеличенный размер шрифта */
}

input, textarea {
  width: 100%;
  padding: 14px; /* Увеличенные отступы */
  margin-bottom: 10px;
  border: 1px solid #ccc;
  border-radius: 8px;
  font-size: 18px; /* Увеличенный размер шрифта */
  box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.1);
}

button {
  background-color: #0088cc;
  color: white;
  border: none;
  padding: 14px 24px; /* Увеличенные отступы */
  border-radius: 8px;
  font-size: 18px; /* Увеличенный размер шрифта */
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.3s, transform 0.3s;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

button:hover {
  background-color: #005f99;
  transform: translateY(-2px);
}

.btn-primary {
  background-color: #0088cc;
}

.btn-secondary {
  background-color: #6c757d;
}

.btn-primary:hover, .btn-secondary:hover {
  transform: translateY(-2px);
}

.card-container {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.card {
  background-color: #ffffff;
  border: 1px solid #e0e0e0;
  border-radius: 16px;
  padding: 30px;
  width: 100%;
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.05);
  transition: box-shadow 0.3s;
}

.card:hover {
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.1);
}

.card-body {
  display: flex;
  flex-direction: column;
}

@media (max-width: 768px) {
  .container {
    width: 90%;
  }
  .card-container {
    flex-direction: column;
    gap: 10px;
  }
}
</style>
