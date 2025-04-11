<template>
  <div class="container mt-4">
    <h2 class="mb-4">Consultar Ementas por dia e/ou por restaurante selecionado:</h2>

    <!-- Filtros -->
    <div class="row mb-4 align-items-end">
      <div class="col-md-4">
        <label class="form-label">Restaurante:</label>
        <select v-model="selectedRestaurantId" class="form-select">
          <option value="">-- Todos os restaurantes --</option>
          <option
            v-for="rest in mealCalendars"
            :key="rest.restaurantId"
            :value="rest.restaurantId"
          >
            {{ rest.restaurantName }}
          </option>
        </select>
      </div>
      <div class="col-md-4">
        <label class="form-label">Data:</label>
        <input
          type="date"
          class="form-control"
          v-model="selectedDate"
        />
      </div>
      <div class="col-md-4 d-flex justify-content-end">
        <button class="btn btn-outline-secondary mt-3 w-100" @click="clearFilters">
        Limpar Filtros
        </button>
      </div>
    </div>

    <!-- Conteúdo filtrado -->
    <div v-if="filteredCalendars.length === 0" class="text-muted">
      Nenhuma ementa encontrada para os filtros selecionados.
    </div>

    <div v-for="rest in filteredCalendars" :key="rest.restaurantId" class="mb-5">
      <h4 class="text-primary">{{ rest.restaurantName }}</h4>

      <table class="table table-bordered mt-2">
        <thead class="table-light">
          <tr>
            <th style="width: 180px;">Dia</th>
            <th>Pratos</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="(meals, date) in getMealsFor(rest)"
            :key="date"
          >
            <td>{{ formatDate(date) }}</td>
            <td>
              <ul class="mb-0">
                <li v-for="meal in meals" :key="meal">{{ meal }}</li>
              </ul>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  data() {
    return {
      mealCalendars: [],
      selectedRestaurantId: '',
      selectedDate: ''
    }
  },
  computed: {
    filteredCalendars() {
      if (!this.selectedRestaurantId) return this.mealCalendars
      return this.mealCalendars.filter(
        r => r.restaurantId === this.selectedRestaurantId
      )
    }
  },
  methods: {
    async fetchAllMenus() {
      try {
        const res = await axios.get('http://localhost:8080/meals/all')
        this.mealCalendars = res.data
      } catch (err) {
        console.error('Erro ao buscar ementas:', err)
      }
    },
    getMealsFor(rest) {
      if (!rest || !rest.mealsByDate) return {}

      let filtered = rest.mealsByDate

      if (this.selectedDate) {
        const result = {}
        if (filtered[this.selectedDate]) {
          result[this.selectedDate] = filtered[this.selectedDate]
        }
        return result
      }

      return Object.keys(filtered)
        .sort()
        .reduce((acc, key) => {
          acc[key] = filtered[key]
          return acc
        }, {})
    },
    formatDate(isoDate) {
      const options = { weekday: 'long', day: 'numeric', month: 'short', year: 'numeric' }
      return new Date(isoDate).toLocaleDateString('pt-PT', options)
    },
    clearFilters() {
      this.selectedRestaurantId = ''
      this.selectedDate = ''
    }
  },
  mounted() {
    this.fetchAllMenus()
  }
}
</script>

<style scoped>
table {
  font-size: 0.95rem;
}
</style>
