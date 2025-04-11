<template>
  <div class="container mt-4">
    <h2>Reserva em Grupo</h2>

    <div class="mb-3">
      <label class="form-label">Número de Pessoas</label>
      <input type="number" v-model.number="peopleCount" class="form-control" min="1" />
    </div>

    <div class="mb-3">
      <label class="form-label">Restaurante</label>
      <select v-model="selectedRestaurantId" class="form-select">
        <option disabled value="">-- Selecione um restaurante --</option>
        <option v-for="restaurant in restaurants" :key="restaurant.id" :value="restaurant.id">
          {{ restaurant.name }}
        </option>
      </select>
    </div>

    <div class="mb-3">
      <label class="form-label">Data</label>
      <input type="date"
             v-model="selectedDate"
             class="form-control"
             :min="today"
             :max="maxForecastDate" />
    </div>

    <div v-if="weatherForecast" class="mb-3">
      <strong>Previsão do tempo:</strong>
      {{ weatherForecast.description }} -
      {{ weatherForecast.temperature.toFixed(1) }}°C
      <img
        :src="`http://openweathermap.org/img/wn/${weatherForecast.icon}.png`"
        alt="icon"
        width="32"
        height="32"
      />
    </div>

    <div v-if="meals.length > 0" class="mt-4">
      <h5>Distribuição dos Pratos</h5>
      <table class="table">
        <thead>
          <tr>
            <th>Prato</th>
            <th>Disponível</th>
            <th>Quantidade</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="meal in meals" :key="meal.id">
            <td>{{ meal.description }}</td>
            <td>{{ meal.availableQuantity }}</td>
            <td>
              <input
                type="number"
                class="form-control"
                v-model.number="meal.selectedQuantity"
                :max="meal.availableQuantity"
                min="0"
                @input="validateQuantities(meal)"
              />
            </td>
          </tr>
        </tbody>
      </table>

      <p><strong>Total selecionado:</strong> {{ totalSelected }} / {{ peopleCount }}</p>

      <button
        class="btn btn-primary"
        :disabled="totalSelected !== peopleCount || hasInvalidQuantities"
        @click="submitGroupReservation"
      >
        Confirmar Reserva de Grupo
      </button>
    </div>

    <div v-if="reservationToken" class="alert alert-success mt-4" ref="successAlert">
      Reserva efetuada com sucesso!<br />
      <strong>Código da reserva:</strong> {{ reservationToken }}
    </div>

    <div v-if="errorMessage" class="alert alert-danger mt-3">
      {{ errorMessage }}
    </div>

    <div v-if="toastMessage" class="toast-container">
      <div class="toast-message" :class="toastType">{{ toastMessage }}</div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  data() {
    const today = new Date()
    const maxDate = new Date()
    maxDate.setDate(today.getDate() + 5)

    return {
      peopleCount: 1,
      selectedRestaurantId: '',
      selectedDate: '',
      restaurants: [],
      meals: [],
      reservationToken: '',
      toastMessage: '',
      toastType: 'success',
      errorMessage: '',
      hasInvalidQuantities: false,
      weatherForecast: null,
      today: today.toISOString().split('T')[0],
      maxForecastDate: maxDate.toISOString().split('T')[0]
    }
  },
  computed: {
    totalSelected() {
      return this.meals.reduce((sum, m) => sum + (m.selectedQuantity || 0), 0)
    }
  },
  watch: {
    selectedDate() {
      this.fetchMeals()
      this.fetchWeather()
    },
    selectedRestaurantId() {
      this.fetchMeals()
    }
  },
  methods: {
    async fetchMeals() {
      this.meals = []
      if (!this.selectedDate || !this.selectedRestaurantId) return

      try {
        const res = await axios.get('http://localhost:8080/meals', {
          params: {
            restaurantId: this.selectedRestaurantId,
            date: this.selectedDate
          }
        })
        this.meals = res.data.map(meal => ({
          ...meal,
          selectedQuantity: 0
        }))
      } catch (err) {
        this.errorMessage = 'Erro ao carregar as ementas.'
      }
    },
    async fetchWeather() {
      if (!this.selectedDate || this.selectedDate.length !== 10) return
      try {
        const res = await axios.get('http://localhost:8080/weather', {
          params: {
            city: 'Aveiro',
            date: this.selectedDate
          }
        })
        this.weatherForecast = res.data
      } catch (err) {
        this.weatherForecast = null
        console.error('Erro ao buscar previsão do tempo:', err)
      }
    },
    async fetchRestaurants() {
      try {
        const res = await axios.get('http://localhost:8080/restaurants')
        this.restaurants = res.data
      } catch (err) {
        this.errorMessage = 'Erro ao carregar restaurantes.'
      }
    },
    validateQuantities(meal) {
      if (meal.selectedQuantity > meal.availableQuantity) {
        this.hasInvalidQuantities = true
        this.showToast(`Quantidade excede o stock para "${meal.description}"`, "error")
      } else {
        this.hasInvalidQuantities = this.meals.some(m => m.selectedQuantity > m.availableQuantity)
      }
    },
    async submitGroupReservation() {
      this.reservationToken = ''
      this.errorMessage = ''

      if (this.totalSelected !== this.peopleCount) {
        this.showToast("A soma dos pratos não corresponde ao número total de pessoas.", "error")
        return
      }

      const payload = {
        restaurantId: this.selectedRestaurantId,
        date: this.selectedDate,
        peopleCount: this.peopleCount,
        meals: this.meals
          .filter(m => m.selectedQuantity > 0)
          .map(m => ({
            description: m.description,
            quantity: m.selectedQuantity
          }))
      }

      try {
        const res = await axios.post('http://localhost:8080/reservations/group', payload)
        this.reservationToken = res.data.token
        this.showToast("Reserva de grupo criada com sucesso!", "success")
        this.meals = []
      } catch (err) {
        this.showToast(err.response?.data || "Erro ao criar reserva de grupo.", "error")
      }
    },
    showToast(message, type = "success") {
      this.toastMessage = message
      this.toastType = type
      setTimeout(() => {
        this.toastMessage = ''
      }, 3000)
    }
  },
  mounted() {
    this.fetchRestaurants()
  }
}
</script>

<style scoped>
.toast-container {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 9999;
}
.toast-message {
  background-color: #28a745;
  color: white;
  padding: 12px 20px;
  border-radius: 6px;
  box-shadow: 0px 4px 10px rgba(0,0,0,0.2);
  animation: fadein 0.3s ease, fadeout 0.5s ease 2.5s;
}
@keyframes fadein {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}
@keyframes fadeout {
  from { opacity: 1; transform: translateY(0); }
  to { opacity: 0; transform: translateY(20px); }
}
.toast-message.success {
  background-color: #28a745;
}
.toast-message.error {
  background-color: #dc3545;
}
</style>