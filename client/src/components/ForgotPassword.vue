<template>
  <v-card v-if="dialog">
    <v-card-title class="text-h5 lighten-2">Forgot My Password</v-card-title>
    <form @submit.prevent="handleSubmit">
      <div class="form-group">
        <!-- Email Form -->
        <v-card-text>비밀번호를 잊어버리셨다면, 해당 이메일을 입력해주세요</v-card-text>
        <v-card-text>해당 메일로 임시비밀번호를 보내 드리겠습니다.</v-card-text>

        <v-text-field
          type="email" label="Email" hide-details="auto"
          v-model="email" id="useremailInput" required/>
        <br>
      </div>

      <v-divider></v-divider>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn type="submit" color="primary" text>
          Submit
        </v-btn>
      </v-card-actions>
    </form>
  </v-card>
</template>

<script>
import axios from "axios"
const SERVER_URL = process.env.VUE_APP_SERVER_URL


export default {
  name: "ForgotPassword",
  data() {
    return {
      email: "",
      dialog: true,
    }
  },
  methods: {
    handleSubmit() {
      axios({
        baseURL: SERVER_URL,
        url: '/users/findpwd',
        method: 'POST',
        data: {"email": this.email}
      })
        .then(this.$emit('close'))
        .catch(e => console.log(e))
    }
  }
}
</script>

<style scoped>

</style>
