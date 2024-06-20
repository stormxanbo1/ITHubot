<script>
import jwt_decode from 'vue-jwt-decode';
import api from '@/api';

export default {
  data() {
    return {
      searchQuery: '',
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
    performSearch() {
      // Здесь можно добавить логику для выполнения поискового запроса
      // Например, можно использовать маршрут или метод для выполнения поиска
      // В этом примере, предположим, что есть маршрут "/search", который отображает результаты поиска
      this.$router.push({ path: '/search', query: { q: this.searchQuery } });
    },
    mainMenu() {
      window.location.href = '/main';
    },

    
    tgHref(){
      window.location.href = 'https://t.me/ITHubbbbot';
    },

    liderboard(){
      window.location.href = '/lead';
    }
    
    
  },
//   mounted() {


//     const token = this.$cookies.get('jwt'); 
//     const decodedToken = jwt_decode.decode(token);

//     if (decodedToken) {
//         this.userId = decodedToken.id;
         
//     } else {
//         console.log('Неверный токен');
//     }



//     try {
//     const response =  api.get('/admin/get/user/'+ this.userId, {
//       headers: {
//         'Authorization': 'Bearer ' + this.$cookies.get('jwt') 
//       }
//     })
//       .then(response => {
//         this.user.name = response.data.username

        
        
//       })
//       .catch(error => {
//         console.error('Ошибка при получении данных:', error);
//       });
      
//     } catch (error) {
//     console.error(error);
//   }
// }
};
</script>

<template>
  <header>
    
    <div class="logo">
      <img
        src="https://cdn-icons-png.flaticon.com/512/2750/2750276.png"
        class="logo"
        alt=""
        @click="mainMenu()"
      />
    </div>
    <div class="buttons">
      <button @click="mainMenu()">Список тестов</button>
      <button @click="liderboard()">Список Лидеров</button>
      <button @click="tgHref()">Телеграмм Бот</button>
      
      
     
    </div>
    
    <div class="UserName">
      <a> {{ this.user.name }}</a>
      <button class="logout" @click="logOut()">Выйти из аккаунта</button>

    </div>
    
    <!-- <div class="search-bar">
      <input type="text" placeholder="Поиск..." v-model="searchQuery" @keyup.enter="performSearch" />
      <button @click="performSearch">Найти</button>
    </div> -->
    
  </header>

  <main></main>
</template>

<style scoped>
.UserName{
  margin-left: 30%;
  color: black;
}
/* .logo{
    width: 15%;
  }
  .logodiv{
    display: flex;
    justify-content: end;
  }
  .cad{
    
    display: flex;
    justify-content: space-around;
    align-items: center;
  } */

header {
  min-width: 60%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 20px;
  background: rgb(255, 255, 255);
  border-radius: 30px;
  background: linear-gradient(
    241deg,
    rgb(255, 255, 255) 0%,
    rgb(253, 237, 255) 100%
  );
  animation: gradient 9s infinite linear;
  background-size: 400%;
  color: white;
}

.logo {
  flex: 1;
  max-width: 50px;
  margin-right: 5%;
}

.search-bar {
  flex: 2;
}

.search-bar input {
  padding: 5px;
  width: 200px;
}

.search-bar button {
  padding: 5px 10px;
  background-color: #45184c;
  color: white;
  border: none;
  cursor: pointer;
}

.buttons {
  flex: 1;
  display: flex;
}

.buttons button {
  

  padding: 5px 10px;
  margin-left: 10px;
  background: none;
  color: rgb(15, 15, 15);
  border: none;
  border-radius: 3px;
  cursor: pointer;
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
  .search-bar input {
    width: 150px;
  }
}

@media (max-width: 480px) {
  header {
    flex-direction: column;
    padding: 10px;
  }
  .search-bar {
    width: 100%;
    align-items: center;
    display: contents;
  }
  .search-bar input {
    width: 100%;
    margin-bottom: 5px;
  }

  .buttons {
    align-items: end;
    margin-top: 10px;
  }

  .buttons button {
    margin-left: 0;
    display: block;
    width: 100%;
    margin-top: 7px;
  }
}
</style>