<template>
  <div class="container">
    <!-- Nested Dialog -->
    <v-dialog v-if="yetPassword" v-model="yetPassword" max-width="360px" persistent>
      <v-card class="pa-3">
        <p class="text-center body-1">해당 회의는 비밀번호가 설정되어 있습니다. <br>비밀번호를 입력해주세요.</p>
        <p class="body-2">비밀번호 문제: {{ conference.question }}</p>
        <v-text-field solo v-model="confPassword"></v-text-field>
        <div class="d-flex justify-space-around" >          
          <v-btn  color="primary" class="my-5" @click="checkParticipants(); inputPassword(confPassword)">
            입력
          </v-btn>
          <v-btn class="my-5" :to="{name: 'Home'}">
            나가기
          </v-btn>
        </div>
      </v-card>
    </v-dialog>
    <!-- 입장 대화상자 -->
    <v-dialog v-else v-model="yetSession" max-width="320px" persistent>
      <v-card class="pa-3">
        <v-card-title>{{conference.title}}</v-card-title>
        <p class="text-center">
          회의에 입장합니다.
        </p>
        

        <v-spacer></v-spacer>

        <div class="d-flex justify-space-around">
          <!-- <v-btn v-if="attendCheck===false" color="primary" class="my-5" @click="checkParticipants() ; attendCheck=true">
            인원체크
          </v-btn> -->
          <v-btn color="primary" class="my-5" @click="enterRoom">
            입장
          </v-btn>
          <v-btn class="my-5" :to="{name: 'Home'}">
            나가기
          </v-btn>
        </div>
      </v-card>

    </v-dialog>

    <!-- 에러 정보 -->
    <v-dialog v-model="errorDialog" max-width="320px">
      <v-card class="pa-3">
        <v-card-tile>에러</v-card-tile>
        <p class="text-center">

        </p>
        <v-btn @click="errorDialog = false">
          확인
        </v-btn>
      </v-card>
    </v-dialog>

    <!-- 회의 화면은 위 대화상자를 통해 입장한 다음 표시됩니다 -->
    <v-row class="align-stretch" v-if="!yetSession">

      <v-col class="col-12">


      </v-col>

      <!-- 영상 목록 -->
      <v-col class="col-12 col-md-8 d-flex flex-column">

        <!-- 사용자 화면 -->
        <div>
          <v-row class="row ma-1">
            <v-col class="justify-center">
              <UserVideo :stream-manager="mainStreamManager" class="current-user-video text-center"/>
              <div class="d-flex justify-space-around flex-grow-1">
                <v-btn class="mx-1" v-show="videoEnabled" @click="publisher.publishVideo(false); videoEnabled = false"
                width="128px">
                  <v-icon>mdi-video-off</v-icon> 화면 끄기</v-btn>
                <v-btn class="mx-1" v-show="!videoEnabled" @click="publisher.publishVideo(true); videoEnabled = true"
                width="128px">
                  <v-icon>mdi-video</v-icon> 화면 켜기</v-btn>
                <v-btn class="mx-1" v-show="audioEnabled" @click="publisher.publishAudio(false); audioEnabled = false"
                width="128px">
                  <v-icon>mdi-microphone-off</v-icon> 음소거</v-btn>
                <v-btn class="mx-1" v-show="!audioEnabled" @click="publisher.publishAudio(true); audioEnabled = true"
                width="128px">
                  <v-icon>mdi-microphone</v-icon> 소리 켜기</v-btn>
              </div>
            </v-col>
            <v-col>
              <v-card>
                <v-card-title>{{ conference.title }}</v-card-title>
                <v-card-text>{{ conference.book.bookName }}</v-card-text>
                <v-card-text>{{ conference.description }}</v-card-text>
              </v-card>
            </v-col>
          </v-row>
        </div>
        <!-- 주 진행자 화면 -->
        <!-- <div>
          <v-card class="ma-1" min-height="480px">주 진행자 화면</v-card>
        </div> -->
        <!-- 기타 청취자 화면 -->
        <v-sheet class="ma-1" min-height="480px">
          <v-slide-group class="pa-1" active-class="success" show-arrows>
            <v-slide-item v-for="sub in subscribers" :key="sub.stream.connection.connectionId" class="ma-2">
              <div>
                <v-menu offset-y>
                  <template v-slot:activator="{attrs, on}">
                    <UserVideo :stream-manager="sub" class="subscribers-video" />
                    <v-card v-bind="attrs" v-on="on">
                      사용자 {{  }}
                    </v-card>
                  </template>
                  <v-list>
                    <v-list-item v-show="sub.stream.videoActive" link @click="sub.subscribeToVideo(false); ">
                      화면 끄기
                    </v-list-item>
                    <v-list-item v-show="!sub.stream.videoActive" link @click="sub.subscribeToVideo(true); ">
                      화면 켜기
                    </v-list-item>
                    <v-list-item v-show="sub.stream.audioActive" link @click="sub.subscribeToAudio(false);">
                      음소거
                    </v-list-item>
                    <v-list-item v-show="!sub.stream.audioActive" link @click="sub.subscribeToAudio(true);">
                      소리 켜기
                    </v-list-item>
                    <v-divider></v-divider>
                    <v-list-item v-if="OV.role === 'MODERATOR'" link @click="kickUser(sub.stream.connection)">
                      강퇴
                    </v-list-item>
                  </v-list>
                </v-menu>

              </div>



            </v-slide-item>
          </v-slide-group>
        </v-sheet>
        
      </v-col>


      <!-- 영상 외 (오른쪽 화면) -->
      <v-col class="col-12 col-md-4 d-flex flex-column">

        
        <Chat />
        <!-- 채팅 화면 -->
        <div class="d-flex flex-column">
          <!-- 채팅창 -->
          <v-card outlined class="overflow-y-auto ma-1 chatlogbox align-end" min-height="560px" max-height="560px">

            <div v-for="(log, idx) in chatlog" :key="idx" :class="log.user === auth.user.nickname ? 'text-start' : 'text-end'">
              <v-card outlined :ripple="false" :color="log.user == auth.user.nickname ? 'green' : null" class="d-inline pa-1">
                {{ log.content }}
              </v-card>
              <p class="caption">
                {{ log.user }} ({{ log.timestamp }})
              </p>
            </div>
          </v-card>
          <!-- 사용자 선택 -->
          <div class="ChatConnection">
            <!-- <v-select label="누구에게 메시지를 보낼까요?" v-model="chatConnection">

            </v-select> -->


            <select v-model="chatConnection">
              <option value="0">모두에게</option>
              <option v-for="sub in subscribers" :key="sub.stream.connection.connectionId" :value="sub.stream.connection">
                {{sub.stream.connection.length !== 0 ? JSON.parse(sub.stream.connection.data.split('%')[0]).clientData : "모두에게"}}
              </option>
            </select>
          </div>
          <!-- 채팅 폼 -->
          <div class="d-flex flex-row">
            <div class="col-8">
            <v-textarea no-resize v-model="inputChat" @keyup.enter.prevent="sendMessage"></v-textarea>
            </div>
            <div class="col-4">
            <v-btn block @click="sendMessage">보내기</v-btn>
            </div>
          </div>
        </div>

        <!-- 기타 조작부 -->
        <v-divider class="my-5"></v-divider>
        <div class="d-flex flex-row justify-space-between">
          <!-- <v-btn>
            환경설정
          </v-btn> -->
          <v-btn color="accent" @click="leaveSession">
            나가기
          </v-btn>
        </div>
      </v-col>


    </v-row>



  </div>
  

  
</template>

<script>
import axios from 'axios'
import moment from 'moment'
import { OpenVidu } from 'openvidu-browser'
import UserVideo from '@/components/conference/UserVideo'
import Chat from '@/components/conference/Chat'
import { mapState } from 'vuex'

axios.defaults.headers.post['Content-Type'] = 'application/json'

// OpenVidu 접속 설정
// 추후에 따로 빼어서 모듈화, 환경변수화해야 할 부분입니다.
const SERVER_URL = process.env.VUE_APP_SERVER_URL


export default {
  name: 'Mainscreen',
  components: {
    Chat, UserVideo,
  },

  props: {
  },

  data: function () {
    return {

      OV: undefined,
      mainStreamManager: undefined,
      publisher: undefined,
      subscribers: [],
      mySessionId: null,

      yetSession: true,
      yetPassword: true,
      session: undefined,      

      participants: 0,
      showMenu: false,

      token: null,
      
      // 회의 정보
      conference: {
      },

      // 비밀번호 입력창
      confPassword: '',

      audioEnabled: undefined,
      videoEnabled: undefined,
      
      
      inputChat: '',
      errorDialog: false,

      chatConnection: 0,
      chatlog: [],

      // ConferenceMeeting 신규 기능 관련 변수
      banTo: 0,
      silenceTo: 0,
      nIntervalId : null,

      // 비동기 문제
      attendCheck: false,

    }
  },

  created: function () {

    if (!this.auth.status.loggedIn) {
      alert('로그인이 필요한 기능입니다.')
      this.yetSession = false
      this.yetPassword = false
      this.$router.push({name: 'Login'})
    }

    this.mySessionId = String(this.$route.params.conferenceId)
    
    // conference init
    this.conference = {
      title: '',
      question: '',
      password: '',
    }

    // 회의 내용 불러오기
    axios({
      method: 'GET',
      baseURL: SERVER_URL,
      url: `/conference/${this.mySessionId}`,

    })
    .then(response => {
      console.log(response)
      if (response.data) {
        this.conference = response.data
        if(response.data.password === null || response.data.password === '') {
          this.yetPassword = false
        }
      }
    })
    .catch(error => {
      console.log(error)
    })

    


    // if (this.conference.password === null && this.conference.password !== '' ) {
    //   this.yetPassword = true
    // } else {
    //   this.yetPassword = false
    //   console.log("password!!",this.conference.password)
    // }
  },

  methods: {
    joinSession: function () {
      this.OV = new OpenVidu()
      this.OV.setAdvancedConfiguration({
        publisherSpeakingEventsOptions: {
          interval: 100,
          threshold: -50,
        },
      })
      this.session = this.OV.initSession()
      this.session.on('streamCreated', ({ stream }) => {
        const subscriber = this.session.subscribe(stream)
        this.subscribers.push(subscriber)
      })
      this.session.on('streamDestroyed', ({ stream }) => {
        const index = this.subscribers.indexOf(stream.streamManager, 0)
        if (index >= 0) {
          this.subscribers.splice(index, 1)
        }
      })

      this.session.on('exception', ({ exception }) => {
        console.warn(exception)
      })

      // 채팅 로그 작성
      this.session.on('signal:my-chat', (event) => {
        console.log(event)
        const timestamp = new Date()
        this.chatlog.push({
          user: `${JSON.parse(event.from.data.split('%')[0]).clientData}`,
          timestamp: `${timestamp.toLocaleString('ko-KR', {timeZone: 'Asia/Seoul'})}`,
          content: `${event.data}`
        })
      })

      // this.session.on('connectionCreated', (event) => {
      //   this.connections.push(event.connection)        
      //   console.log("connections:", this.connections)
      // })

      // this.session.on('connectionDestroyed', (event)=> {
      //   console.log("disconnection:", event)
      //   const index = this.connections.indexOf(event.connection, 0)
      //   console.log("index:", index)
      //   if (index >= 0) {
      //     this.connections.splice(index, 1)
      //   }
      // })

      this.session.on('signal:kick-msg', () => {        
        this.leaveSession()       
      })


      //수정?
      this.session.on('publisherStartSpeaking', (event) => {
          console.log('User ' + event.connection.connectionId + ' start speaking');
      });

      this.session.on('publisherStopSpeaking', (event) => {
          console.log('User ' + event.connection.connectionId + ' stop speaking');
      });



      // user token
      this.getToken(this.mySessionId).then(token => {
        console.log('token: ', token)
        this.token = token
        // clientData: 접속하는 사람의 정보 입력
        this.session.connect(token, { clientData: this.auth.user.nickname})
          .then(() => {
            let publisher = this.OV.initPublisher(undefined, {
              audioSource: undefined, // The source of audio. If undefined default microphone
							videoSource: undefined, // The source of video. If undefined default webcam
							publishAudio: true,  	// Whether you want to start publishing with your audio unmuted or not
							publishVideo: true,  	// Whether you want to start publishing with your video enabled or not
							resolution: '640x480',  // The resolution of your video
							frameRate: 30,			// The frame rate of your video
							insertMode: 'APPEND',	// How the video is inserted in the target element 'video-container'
							mirror: false       	// Whether to mirror your local video or not
            })

            this.videoEnabled = publisher.publishVideo
            this.audioEnabled = publisher.publishAudio

            this.mainStreamManager = publisher
            this.publisher = publisher

            this.session.publish(this.publisher)
          })
          .catch(error => {
            console.log('There was an error connecting to the session:', error.code, error.message);
          })
      })

      window.addEventListener('beforeunload', this.leaveSession)
    },

    // 위까지가 joinSession

    leaveSession () {
      if (this.session) {
        axios({
          baseURL: SERVER_URL,
          method: 'delete',
          url: `conference/${this.mySessionId}/leave`,
          headers: {
            'X-AUTH-TOKEN': this.auth.user.token
          },
          data: {
            token: this.token
          },
        })
        .then(response => {
          console.log(response)
        })
        .catch(error => console.log(error))
        .finally(
          this.session.disconnect()
        )
      }
      
      this.session = undefined;
			this.mainStreamManager = undefined;
			this.publisher = undefined;
			this.subscribers = [];
			this.OV = undefined;
      this.nIntervalId = null;

      window.removeEventListener('beforeunload', this.leaveSession);

      // 세션 종료 후 이동
      this.$router.push({ name: 'Home'})
    },

    updateMainVideoStreamManager (stream) {
      if (this.mainStreamManager === stream) return;
      this.mainStreamManager = stream;
    },

    // 여기서부터 server와 상호작용
    getToken (mySessionId) {
      return this.createToken(mySessionId)
    },

    // createSession (sessionId) {
    //   return new Promise((resolve, reject) => {
    //     axios
    //       .post(`${OPENVIDU_SERVER_URL}/openvidu/api/sessions`, JSON.stringify({
    //         customSessionId: sessionId,
    //       }), {
    //         auth: {
    //           username: 'OPENVIDUAPP',
    //           password: OPENVIDU_SERVER_SECRET,
    //         },
    //       })
    //       .then(response => response.data)
    //       .then(data => resolve(data.id))
    //       .catch(error => {
    //         if(error.response.status === 409) {
    //           resolve(sessionId)
    //         } else {
    //           console.warn(`No connection to OpenVidu Server. This may be a certificate error at ${OPENVIDU_SERVER_URL}`);
		// 					if (window.confirm(`No connection to OpenVidu Server. This may be a certificate error at ${OPENVIDU_SERVER_URL}\n\nClick OK to navigate and accept it. If no certificate warning is shown, then check that your OpenVidu Server is up and running at "${OPENVIDU_SERVER_URL}"`)) {
		// 						location.assign(`${OPENVIDU_SERVER_URL}/accept-certificate`);
    //           }
		// 					reject(error.response);
    //         }
    //       })
    //   })
    // },

    //여기까지 createSession

    createToken (sessionId) {
			return new Promise((resolve, reject) => {
        axios({
          method: 'GET',
          baseURL: SERVER_URL,
          url: `/conference/${sessionId}/token`,
          headers: {
            'X-AUTH-TOKEN': this.auth.user.token
          },
        })
        .then(response => {
          
          console.log('ok: ', response.data)
          return response.data
          
        })
        .then(data => resolve(data))
        .catch(error => reject(error.response))
        })
    },


    onKick: function (userid) {
      console.log('kick' + userid)
    },

    // onInputChat: function () {
    //   if (this.inputChat != '') {
    //     console.log('input:' + this.inputChat)

    //     this.chatlog.push({user: '지금 사용자', timestamp: '지금 시간', content: this.inputChat})
    //     this.inputChat = ''
    //   }
    // },

    sendMessage: function () {
      // 공백은 전달하지 않습니다.
      if (this.inputChat.trim() === '') {
        this.inputChat= ''
        return
      }

      if(this.chatConnection === 0 || this.chatConnection === '0') {
        this.session.signal({
          data: this.inputChat,
          to: [],
          type: 'my-chat'
        })
        .then(() => {          
          console.log('Message successfully sent')
          this.inputChat = ''
        })
        .catch(error => {
          console.error(error)
        })
      } else {
        this.session.signal({
          data: this.inputChat,
          to: [this.publisher.stream.connection, this.chatConnection],
          type: 'my-chat'
        })
        .then(() => {          
          console.log('Private Message successfully sent')
          this.inputChat = ''
        })
        .catch(error => {
          console.error(error)
        })
      }
    },

    inputPassword: function (str) {
      if (str === this.conference.password) {
        this.yetPassword = false
      } else {
        alert("비밀번호가 틀렸습니다.")
        this.confPassword = ''
      }
    },

    // ConferenceMeeting 신규 기능
    kickUser(someConnection){
      console.log("connection:", someConnection)
      
      this.session.signal({
        to: [someConnection],
        type: 'kick-msg'
      })
      .then(res => console.log(res))
      .catch(err => console.error(err))
      
      this.session.forceDisconnect(someConnection)
      
      
    },
    republish(){
      this.session.publish(this.publisher)
    },
    unpublish(){
      this.session.unpublish(this.publisher)
    },
    makeSilence(){
      console.log(this.subscribers[this.silenceTo].stream)
      this.session.forceUnpublish(this.subscribers[this.silenceTo].stream)
      this.silenceTo = 0
    },
    checkTime(){
      const nowMoment = moment()
      return (moment(this.conference.callStartTime).isBefore(nowMoment) && nowMoment.isBefore(moment(this.conference.callEndTime)))
    },
    checkParticipants(){
      axios({
        baseURL: SERVER_URL,
        method: 'get',
        url: `/conference/${this.conference.id}/attend`
      })
      .then(res=>{
        this.participants = res.data.data
        console.log("p-check",res)})
      .catch(err=>console.error(err))
    },
    enterRoom(){
      this.checkParticipants()
      if(this.checkTime()){
        if(this.participants === 0){
          if (this.auth.user.id === this.conference.user.id){
            this.joinSession()
            this.yetSession = false
            this.checkTimeOut()
          } else {
            alert('개설자가 회의를 개설할 때까지 기다려주세요.')
          }
        } else if(this.participants < this.conference.maxMembers) {
          this.joinSession()
          this.yetSession = false
          this.checkTimeOut()
        } else {
          alert('인원이 초과되었습니다.')
        }
      } else {
        alert('회의에 참석할 수 있는 시간이 아닙니다.')
      }
    },

    checkTimeOut(){
      if(!this.nIntervalId){
        this.nIntervalId = setInterval(this.endTimeCheck,60000)
        console.log("active!!!")
      }
    },
    endTimeCheck(){

      const now = moment()
      const isEndtime = moment(this.conference.callEndTime).isBefore(now)
      if( isEndtime ){
        clearInterval(this.nIntervalId)
        this.nIntervalId = null
        this.leaveSession()
        alert('종료시간이 되었습니다.')
      }
    }


    ////




  },
  computed: {
    
    // chatList: function () {
    //   let connectionList = this.session
    //   return ['모두에게'] + connectionList
    // }
    ...mapState(['auth'])
  },

  watch: {
    chatlog: function () {
      const chatlogbox = document.querySelector('.chatlogbox')
      chatlogbox.scrollTop = chatlogbox.scrollHeight
    },
  }


}
</script>

<style>
  .current-user-video video {
    max-width: 320px;
  }

  .subscribers-video video {
    max-width: 240px;
  }
</style>