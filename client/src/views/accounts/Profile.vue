<template>
  <v-container class="profileForm" style="padding-top:5rem;">
    <v-row>
      <v-col class="text-center align-self-center justify-center text-center" cols="3">
        <v-avatar v-if="user.profileImage" size="150">
          <v-img :src="user.profileImage" contain />
        </v-avatar>
        <v-avatar v-else size="150">
          <v-img src="@/assets/host_img/HostImg.png" />
        </v-avatar>
      </v-col>
      <v-col cols="8" style="margin-left: 2rem">
        <h1 style="display:inline;">{{ user.nickname }}님의 개인 프로필</h1>
        <br><br><br>
        <v-card>
          <v-card-title><strong>한마디</strong></v-card-title>
          <v-spacer></v-spacer>
          <v-card-subtitle v-if="user.profileDescription.length > 0">{{ user.profileDescription }}</v-card-subtitle>
          <v-card-subtitle v-else>아직 한마디가 없어요..</v-card-subtitle>
        </v-card>
        <br>
        <div class="d-flex">
          <h3 v-if="conferences.length">{{ conferences.length }}개의 모임에 참가했습니다.</h3>
          <v-img :src="require(`@/assets/bookicon/book${iconUrl}page.svg`)" style="height:70px;" contain />
        </div>
      </v-col>
    </v-row>
    <v-col style="padding-top:2rem">
      <v-item-group v-if="!conferences.length" style="text-align-last: center">
        <h2 style="color: #ff3170;">아직 함께 참여한 모임이 없어요 ㅠㅠ </h2>
        <br>
        <v-btn v-if="searchUser === this.$store.state.auth.user.id" rounded class="primary" href="conference">참여하러가기</v-btn>
      </v-item-group>
      <div v-else>
        <h3>북마크한 모임</h3>
        <v-item-group>
          <v-container>
            <v-row>
              <v-col cols="6" v-for="(item, idx) in conferences.slice(0, 5)" :key="idx">
                <v-item>
                  <v-card
                    class="bookcard"
                    max-width="0">
                    <template>
                      <div class="book">
                        <div class="back" />
                        <div class="page6">
                          <v-card-text style="padding:0;">
                            <v-card-title style="font-size:15px; font-weight:bold;">{{ item.conference.title }}</v-card-title>
                            <v-card-subtitle >
                              모임 날짜: {{ item.conference.callStartTime.slice(0, 10) }}
                              <hr>
                              시작 시간: {{ item.conference.callStartTime.slice(11, 19) }}
                              <br>
                              종료 시간: {{ item.conference.callEndTime.slice(11, 19) }}
                            </v-card-subtitle>
                          </v-card-text>
                          <!-- <div class="container" style="text-align: center;">
                            <v-btn class="primary" v-if="isLive(item.conference.id)" @click="GoToConference(item.conference.id)">참여하기</v-btn>
                          </div> -->
                        </div>
                        <div class="page5" /><div class="page4" /><div class="page3" /><div class="page2" /><div class="page1" />
                        <div class="front">
                          <v-img :src="item.conference.book.bookThumbnailUrl" contain />
                        </div>
                      </div>
                    </template>
                  </v-card>
                </v-item>
              </v-col>
            </v-row>
          </v-container>
        </v-item-group>
      </div>
    </v-col>
  </v-container>
</template>

<script>
import axios from "axios"
const SERVER_URL = process.env.VUE_APP_SERVER_URL

export default {
  name: 'Profile',
  data() {
    return {
      user: {},
      conference: '',
      conferences: [],
      searchUser : null,
      iconUrl: '1',
      cnt: 0,
      num: '',

    }
  },
  methods: {
    onclick(item) {
      this.conference = item
    },
    // isLive(id) {
    //   axios({
    //     baseURL: SERVER_URL,
    //     url: `/conference/${id}/live`,
    //     method: 'GET',
    //   }).then(res => {
    //     return res.data
    //     })
    // },
    GoToConference(id) {
      this.$router.push({path: `/conference/${id}/main`})
    },
    userProfile() {
      if (this.searchUser === undefined || this.searchUser === null) this.searchUser = this.$store.state.auth.user.id
      axios({
        baseURL: SERVER_URL,
        url: '/users/' + this.searchUser,
        method: 'GET',
        headers: {
            'X-AUTH-TOKEN': this.$store.state.auth.user.token
          },
      })
      .then(res => {
        this.user = res.data
        return
      })
      .catch(() => {})
    },
    userBookmark() {
      axios({
        baseURL: SERVER_URL,
        url:`/users/${this.searchUser}/bookmark`,
        method: 'GET',
      })
      .then(res => {
        this.conferences = res.data
        if (this.conferences.length >= 500) {
          this.iconUrl = "500"
        } else if (this.conferences.length >= 250) {
          this.iconUrl = "250"
        } else if (this.conferences.length >= 100) {
          this.iconUrl = "100"
        } else if (this.conferences.length >= 50) {
          this.iconUrl = "50"
        } else if (this.conferences.length >= 25) {
          this.iconUrl = "25"
        } else if (this.conferences.length >= 10) {
          this.iconUrl = "10"
        } else if (this.conferences.length >= 5) {
          this.iconUrl = "50"
        } else{
          this.iconUrl = "1"
        }
        })
      .catch(e => console.log(e))
    },
  },
  beforeMount() {
    if (this.$route.query.data !== undefined) this.searchUser = JSON.parse(this.$route.query.data).userId
    this.userProfile()
  },
  mounted() {
    this.userBookmark()
  }
}
</script>

<style>

.img-upload > input {
  display: none;
}

.book {
  transform-style: preserve-3d;
  position: relative;
  width: 170px;
  height: 247px;
  margin: 1rem;
  cursor: pointer;
  backface-visibility: visible;
}

.front, .back, .page1, .page2, .page3, .page4, .page5, .page6 {
  transform-style: preserve-3d;
  position: absolute;
  width: 170px;
  height: 247px;
  transform-origin: right center;
  transition: transform .5s ease-in-out, box-shadow .35s ease-in-out;
}

.front, .page1, .page3, .page5 {
  border-bottom-right-radius: .5em;
  border-top-right-radius: .5em;
}

.back, .page2, .page4, .page6 {
  border-bottom-right-radius: .5em;
  border-top-right-radius: .5em;
}

.page1 { 
  background: #efefef;
}

.page2 {
  background: #efefef;
}

.page3 {
  background: #f5f5f5;
}

.page4 {
  background: #f5f5f5;
}

.page5 {
  background: #fafafa;
  display: flex;
  flex-direction: column-reverse;
}

.page6 {
  background: #fdfdfd;
}

.book:hover .front {
  transform: rotateY(160deg) scale(1.1);
  box-shadow: 0 1em 3em 0 rgba(0, 0, 0, .2);
}

.book:hover .page1 {
  transform: rotateY(150deg) scale(1.1);
  box-shadow: 0 1em 3em 0 rgba(0, 0, 0, .2);
}

.book:hover .page2 {
  transform: rotateY(30deg) scale(1.1);
  box-shadow: 0 1em 3em 0 rgba(0, 0, 0, .2);
}

.book:hover .page3 {
  transform: rotateY(140deg) scale(1.1);
  box-shadow: 0 1em 3em 0 rgba(0, 0, 0, .2);
}

.book:hover .page4 {
  transform: rotateY(40deg) scale(1.1);
  box-shadow: 0 1em 3em 0 rgba(0, 0, 0, .2);
}

.book:hover .page5 {
  transform: rotateY(130deg) scale(1.1);
  box-shadow: 0 1em 3em 0 rgba(0, 0, 0, .2);
}

.book:hover .page6 {
  transform: rotateY(45deg) scale(1.1);
  box-shadow: 0 1em 3em 0 rgba(0, 0, 0, .2);
}

.book:hover .back {
  transform: rotateY(20deg) scale(1.1);
}
</style>
