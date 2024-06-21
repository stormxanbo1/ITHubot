<script>
import api from "../api.js";
import { useVuelidate } from "@vuelidate/core";
import { required } from "@vuelidate/validators";

export default {
  setup() {
    return { v$: useVuelidate() };
  },

  data() {
    return {
      users: [],
      userName: "",
      userPassword: "",
      errorMessage: "",
    };
  },
  validations() {
    return {
      userName: { required },
      userPassword: { required },
    };
  },
  methods: {
    async signupUser() {
      const newUser = {
        name: this.userName,
        password: this.userPassword,
      };
      const isFormCorrect = await this.v$.$validate();
      if (isFormCorrect) {
        try {
          const response = await api.post("/main/signup", newUser);
          console.log("Успешно зарегистрирован:", newUser);
          window.location.href = "/log";
        } catch (error) {
          if (error.response && error.response.status == 401) {
            this.errorMessage = "Ошибка на стороне сервера!!!";
          } else {
            this.errorMessage = "Ошибка авторизации! Проверьте логин и пароль";
          }
          console.log(this.userName);
        }
      }
    },
  },
};
</script>

<template>
  <div class="All">
    <header>
      <h2>{{ errorMessage }}</h2>
    </header>
    <main>
      <div class="reg_window">
        <div class="reg_input">
          <input type="text" v-model.trim="userName" placeholder="Имя" />
          <div v-if="v$.userName.$error">Поле имени обязательно.</div>

          <input
            type="password"
            v-model.trim="userPassword"
            placeholder="Пароль"
          />
          <div v-if="v$.userPassword.$error">Поле пароля обязательно.</div>
        </div>
        <div class="reg_Button">
          <button class="regBT" @click="signupUser()">Отправить</button>
        </div>
      </div>
    </main>
    <footer></footer>
  </div>
</template>

<style scoped>
.All {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100vh;
  background: linear-gradient(135deg, #ffafbd, #ffc3a0);
  font-family: 'Helvetica Neue', sans-serif;
}

header {
  font-size: 20px;
  color: red;
  margin-bottom: 20px;
}

.reg_window {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 20px;
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.3);
  padding: 40px;
  text-align: center;
  width: 300px;
}

.reg_input {
  margin-bottom: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.reg_input input {
  margin-bottom: 10px;
  padding: 10px;
  width: 100%;
  font-size: 18px;
  border: 1px solid #ccc;
  border-radius: 10px;
  box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: border-color 0.3s, box-shadow 0.3s;
}

.reg_input input:focus {
  border-color: #ff6f61;
  box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.2);
}

.reg_Button {
  display: flex;
  justify-content: center;
}

.regBT {
  padding: 12px 24px;
  font-size: 20px;
  color: #fff;
  background: linear-gradient(135deg, #ff6f61, #d15b5b);
  border: none;
  border-radius: 30px;
  cursor: pointer;
  transition: background 0.3s, box-shadow 0.3s;
  width: 100%;
}

.regBT:hover {
  background: linear-gradient(135deg, #ff857a, #e76e6e);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.error {
  font-size: 16px;
  color: red;
  margin-top: 10px;
}
</style>
