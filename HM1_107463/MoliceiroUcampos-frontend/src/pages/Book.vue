<template>
  <div class="container mt-4">
    <h2>1. Escolha o Restaurante</h2>

    <div class="row mb-3">
      <div class="col-md-6">
        <input
          v-model="searchQuery"
          type="text"
          class="form-control"
          placeholder="Pesquisar restaurante"
        />
      </div>
      <div class="col-md-3">
        <button class="btn btn-outline-secondary w-100" @click="clearFilters">
          Limpar Filtros
        </button>
      </div>
    </div>

    <div class="row">
      <RestaurantCard
        v-for="restaurant in filteredRestaurants"
        :key="restaurant.id"
        :restaurant="restaurant"
        :isSelected="restaurant.id === selectedRestaurantId"
        @select="onRestaurantSelected"
      />
    </div>

    <hr class="my-5" />

    <div v-if="selectedRestaurant" class="card p-4 shadow-sm mb-4">
      <h2>2. Selecione o Dia</h2>
      <input
        type="date"
        class="form-control w-50 mb-3"
        v-model="selectedDate"
        :min="minDate"
        :max="maxDate"
      />

      <div v-if="selectedDate">
        <h2>3. Escolha o Menu</h2>

        <div v-if="meals.length > 0">
          <select class="form-select w-50 mb-3" v-model="selectedMenu">
            <option disabled value="">Selecione um menu</option>
            <option
              v-for="meal in meals"
              :key="meal.id"
              :value="meal.description"
              :disabled="meal.availableQuantity <= 0"
            >
              {{ meal.description }} <span v-if="meal.availableQuantity <= 0">(Esgotado)</span>
            </option>
          </select>
        </div>

        <div v-else class="text-muted mb-3">
          Nenhum prato disponível para esta data neste restaurante.
        </div>
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

      <button class="btn btn-success mt-3" @click="bookMeal">
        Confirmar Reserva
      </button>
    </div>

    <div
      v-if="reservationToken"
      class="alert alert-success mt-4"
      ref="successAlert"
    >
      Reserva efetuada com sucesso!<br />
      <strong>Código da reserva:</strong> {{ reservationToken }}
    </div>

    <!-- Modal de erro -->
    <div class="modal fade" id="errorModal" tabindex="-1" aria-hidden="true">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header bg-danger text-white">
            <h5 class="modal-title">Erro ao Reservar</h5>
            <button
              type="button"
              class="btn-close btn-close-white"
              data-bs-dismiss="modal"
              aria-label="Fechar"
            ></button>
          </div>
          <div class="modal-body">
            {{ modalMessage }}
          </div>
          <div class="modal-footer">
            <button
              type="button"
              class="btn btn-secondary"
              data-bs-dismiss="modal"
            >
              Fechar
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import RestaurantCard from '../components/RestaurantCard.vue'
import axios from 'axios'

export default {
  components: {
    RestaurantCard
  },
  data() {
    const today = new Date()
    const max = new Date()
    max.setDate(today.getDate() + 5)

    const toISO = (d) => d.toISOString().split('T')[0]

    return {
      searchQuery: '',
      selectedRestaurant: null,
      selectedRestaurantId: null,
      selectedDate: '',
      selectedMenu: '',
      reservationToken: '',
      restaurants: [],
      meals: [],
      modalMessage: '',
      weatherForecast: null,
      minDate: toISO(today),
      maxDate: toISO(max)
    }
  },
  computed: {
    filteredRestaurants() {
      return this.restaurants.filter(r =>
        r.name.toLowerCase().includes(this.searchQuery.toLowerCase())
      )
    }
  },
  watch: {
    selectedDate(newDate) {
      if (this.selectedRestaurant && newDate && newDate.length === 10) {
        this.fetchMeals()
        this.fetchWeather()
      }
    },
    selectedRestaurant() {
      this.fetchMeals()
    }
  },
  mounted() {
    this.fetchRestaurants()
  },
  methods: {
    async fetchRestaurants() {
      try {
        const response = await axios.get('http://localhost:8080/restaurants')
        this.restaurants = response.data
      } catch (error) {
        console.error('Erro ao buscar restaurantes:', error)
      }
    },
    async fetchMeals() {
      this.meals = []
      if (!this.selectedDate || !this.selectedRestaurant) return

      try {
        const res = await axios.get('http://localhost:8080/meals', {
          params: {
            restaurantId: this.selectedRestaurant.id,
            date: this.selectedDate
          }
        })
        this.meals = res.data
      } catch (error) {
        console.error('Erro ao buscar ementas:', error)
      }
    },
    async fetchWeather() {
      if (!this.selectedDate || this.selectedDate.length !== 10) return

      try {
        const response = await axios.get('http://localhost:8080/weather', {
          params: {
            city: 'Aveiro',
            date: this.selectedDate
          }
        })
        this.weatherForecast = response.data
      } catch (error) {
        console.error('Erro ao buscar previsão do tempo:', error)
        this.weatherForecast = null
      }
    },
    clearFilters() {
      this.searchQuery = ''
      this.selectedRestaurant = null
      this.selectedRestaurantId = null
      this.selectedDate = ''
      this.selectedMenu = ''
      this.meals = []
      this.reservationToken = ''
      this.weatherForecast = null
    },
    onRestaurantSelected(restaurant) {
      this.selectedRestaurant = restaurant
      this.selectedRestaurantId = restaurant.id
      this.reservationToken = ''
    },
    async bookMeal() {
      if (!this.selectedDate || !this.selectedMenu) {
        this.modalMessage = !this.selectedDate && !this.selectedMenu
          ? 'Por favor selecione a data e o prato.'
          : !this.selectedDate
          ? 'Por favor selecione a data da refeição.'
          : 'Por favor selecione um prato.'

        const modal = new bootstrap.Modal(document.getElementById('errorModal'))
        modal.show()
        return
      }

      const payload = {
        restaurantId: this.selectedRestaurant.id,
        date: this.selectedDate,
        menu: this.selectedMenu
      }

      try {
        const res = await axios.post('http://localhost:8080/reservations', payload)
        this.reservationToken = res.data.token

        this.$nextTick(() => {
          this.$refs.successAlert?.scrollIntoView({ behavior: 'smooth' })
        })
      } catch (err) {
        this.modalMessage = 'Erro ao fazer a reserva. Tenta novamente.'
        const modal = new bootstrap.Modal(document.getElementById('errorModal'))
        modal.show()
        console.error(err)
      }
    }
  }
}
</script>

<style scoped>
.card {
  background-color: #fdfdfd;
  border: 1px solid #e3e3e3;
}
</style>