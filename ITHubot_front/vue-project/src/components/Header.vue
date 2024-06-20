<script>
import jwt_decode from 'vue-jwt-decode';
import api from '@/api';

export default {
  data() {
    return {
      user: {
        name: '',  
      },
      userId: null
    };
  },
  methods: {
    logOut(){
      this.$cookies.remove('jwt');
      window.location.href="/log"
    },
    mainMenu() {
      window.location.href = '/main';
    },
    tgHref(){
      window.location.href = 'https://t.me/ITHubbbbot';
    },
    leaderboard(){
      window.location.href = '/lead';
    },
  //   async fetchUserData() {
  //     const token = this.$cookies.get('jwt'); 
  //     const decodedToken = jwt_decode.decode(token);
  //     if (decodedToken) {
  //       this.userId = decodedToken.id;
  //       try {
  //         const response = await api.get('/admin/get/user/' + this.userId, {
  //           headers: {
  //             'Authorization': 'Bearer ' + token
  //           }
  //         });
  //         this.user.name = response.data.username;
  //       } catch (error) {
  //         console.error('Ошибка при получении данных:', error);
  //       }
  //     } else {
  //       console.log('Неверный токен');
  //     }
  //   }
  // },
  // mounted() {
  //   this.fetchUserData();
  // }
}};
</script>

<template>
  <header>
    <div class="logo">
      <img
        src="https://png.pngtree.com/png-clipart/20230824/original/pngtree-chat-bot-icon-vector-picture-image_8418981.png"
        class="logo-img"
        alt="New Logo"
        @click="mainMenu()"
      />
    </div>
    <div class="buttons">
      <button @click="mainMenu()">Список тестов</button>
      <button @click="leaderboard()">Список Лидеров</button>
      <button @click="tgHref()">Телеграмм Бот</button>
    </div>
    <div class="user-info">
      <span>{{ user.name }}</span>
      <button class="logout" @click="logOut()">Выйти из аккаунта</button>
    </div>
  </header>
</template>

<style scoped>
header {
  min-width: 90%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 20px;
  background: linear-gradient(241deg, rgb(255, 255, 255), rgb(253, 237, 255));
  border-radius: 30px;
  animation: gradient 9s infinite linear;
  background-size: 400%;
  color: white;
}

.logo {
  flex: 1;
}

.logo-img {
  max-width: 100px;
  cursor: pointer;
}

.buttons {
  flex: 1;
  display: flex;
  justify-content: flex-end;
}

.buttons button {
  margin-left: 10px;
  padding: 8px 16px;
  background: none;
  color: #0f0f0f;
  border: none;
  border-radius: 3px;
  cursor: pointer;
}

.user-info {
  flex: 1;
  display: flex;
  justify-content: flex-end; 
  align-items: center;
}

.user-info span {
  margin-right: 10px;
  color: black;
}

.logout {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 3px;
  cursor: pointer;
}

.logout:hover {
  background-color: #0056b3;
}

@keyframes gradient {
  0% {
    background-position: 80% 0%;
  }
  50% {
    background-position: 20% 100%;
  }
  100% {
    background-position: 80% 0%;
  }
}

@media (max-width: 768px) {
  header {
    flex-direction: column;
    padding: 10px;
  }

  .buttons {
    flex-direction: column;
    align-items: center;
    margin-top: 10px;
  }

  .buttons button {
    margin-left: 0;
    display: block;
    width: 100%;
    margin-top: 7px;
  }

  .user-info {
    flex-direction: column;
    align-items: center;
    margin-top: 10px;
  }

  .user-info button {
    margin-top: 7px;
  }
}
</style>
