<template>
  <div class="container mt-4">
    <h2>📊 Monitorização da Cache Meteorológica</h2>

    <div v-if="loading" class="alert alert-info mt-3">A carregar dados...</div>
    <div v-if="error" class="alert alert-danger mt-3">{{ error }}</div>

    <div v-if="stats && !loading && !error" class="mt-4">
      <ul class="list-group mb-4">
        <li class="list-group-item d-flex justify-content-between align-items-center">
          Total de Pedidos
          <span class="badge bg-primary rounded-pill">{{ stats.requests }}</span>
        </li>
        <li class="list-group-item d-flex justify-content-between align-items-center">
          Hits (acertos na cache)
          <span class="badge bg-success rounded-pill">{{ stats.hits }}</span>
        </li>
        <li class="list-group-item d-flex justify-content-between align-items-center">
          Misses (falhas)
          <span class="badge bg-danger rounded-pill">{{ stats.misses }}</span>
        </li>
      </ul>
    </div>
    <div v-if="cacheEntries.length > 0" class="mt-5">
      <h4>🔒 Entradas na Cache</h4>
      <table class="table table-bordered">
        <thead>
          <tr>
            <th>Chave</th>
            <th>Descrição</th>
            <th>Temperatura (°C)</th>
            <th>Ícone</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="[key, entry] in cacheEntries" :key="key">
            <td>{{ key }}</td>
            <td>{{ entry.description }}</td>
            <td>{{ entry.temperature }}</td>
            <td><img :src="`http://openweathermap.org/img/wn/${entry.icon}@2x.png`" alt="Ícone" width="32" /></td>
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
      stats: null,
      loading: true,
      error: null,
      intervalId: null,
      cacheEntries: []
    }
  },
  computed: {
    hitRate() {
      if (!this.stats || this.stats.requests === 0) return 0
      return ((this.stats.hits / this.stats.requests) * 100).toFixed(1)
    },
    missRate() {
      if (!this.stats || this.stats.requests === 0) return 0
      return ((this.stats.misses / this.stats.requests) * 100).toFixed(1)
    }
  },
  methods: {
    async fetchStats() {
      this.loading = true
      this.error = null
      try {
        const res = await axios.get('http://localhost:8080/weather-monitor/cache/stats')
        this.stats = res.data
      } catch (err) {
        this.error = 'Erro ao carregar estatísticas da cache.'
        console.error(err)
      } finally {
        this.loading = false
      }
    },
    async fetchCacheEntries() {
  try {
    const res = await axios.get("http://localhost:8080/weather-monitor/cache/entries");
    this.cacheEntries = Object.entries(res.data);
  } catch (err) {
    console.error("Erro ao buscar entradas da cache:", err);
  }
}

  },
  mounted() {
    this.fetchStats()
    this.fetchCacheEntries()
    this.intervalId = setInterval(() => {
      this.fetchStats()
      this.fetchCacheEntries()
    }, 5000)
  },
  beforeUnmount() {
    clearInterval(this.intervalId)
  }
}
</script>

<style scoped>
.badge {
  font-size: 1rem;
}
</style>
