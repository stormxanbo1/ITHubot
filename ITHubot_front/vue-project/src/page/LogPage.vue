<script>
import api from '../api.js';
import { useVuelidate } from '@vuelidate/core';
import { required } from '@vuelidate/validators';


export default {
  

  setup () {
    return { v$: useVuelidate() }
  },
  data() {
    return {
      users: [],
      userName: '',
      errorMessage: '',
      responseData: null,
      userPassword: '',
      jwt: null,
    };
  },
  validations () {
    return {
      userName: { required }, // Matches this.firstName
      userPassword: { required }, // Matches this.lastName
      
      
    } 
  },
  methods: {

    setPage(){
      window.location.href="/reg";
    },



    async signupUser() {
      const User = {
        username: this.userName,
        password: this.userPassword,
      };
      const isFormCorrect = await this.v$.$validate()
      if (isFormCorrect){
        try {
          const response = await api
            .post('/secured/signin', User)
            .then((response) => {
              this.responseData = response.data;
              this.errorMessage = response.data.message;
              this.jwt = response.data;
              this.$cookies.remove('jwt');
              this.$cookies.set('jwt', this.jwt, '1d');
             
              window.location.href = '/main';
            })
            .catch((error) => {
              console.error(error);
              this.errorMessage = error.response.status;
            });
          
        } catch (error) {}
      }
    },
  },

};
</script>

<template>
  <div class="All">
    <header></header>
    <main>
      <div class="reg_window">
        <div class="reg_input">
          <h2 class="error">{{ errorMessage }}</h2>
          <input type="text" v-model.trim="userName" placeholder="name" />

          <input
            type="password"
            v-model.trim="userPassword"
            placeholder="password"
          />
        </div>
        <div class="reg_Button">
          <button class="regBT" @click="signupUser()">отправить</button>

        </div>
        <div class="regist"><a class="regi" @click="setPage()">зарегистрироваться</a></div>
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

header, footer {
  display: none;
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
}

.reg_input input {
  margin-bottom: 20px;
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

.regist {
  margin-top: 20px;
}

.regi {
  color: #ff6f61;
  text-decoration: none;
  font-size: 18px;
  transition: color 0.3s;
}

.regi:hover {
  color: #d15b5b;
}

.error {
  font-size: 18px;
  color: red;
  margin-bottom: 20px;
}
</style>
