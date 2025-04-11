
<template>
  <div class="container mt-5">
    <h2>Checkin Refeições</h2>

    <div class="mb-3">
      <input
        v-model="token"
        type="text"
        class="form-control"
        placeholder="Insere o código da reserva"
      />
    </div>

    <button class="btn btn-primary mb-3" @click="getReservations" :disabled="!token">
     Carregar Reserva
    </button>

    <div v-if="errorMessage" class="alert alert-danger">
      {{ errorMessage }}
    </div>

    <div v-if="reservations.length" class="mt-4">
      <h5>Detalhes da Reserva</h5>
      <table class="table">
        <thead>
          <tr>
            <th>Restaurante</th>
            <th>Data</th>
            <th>Menu</th>
            <th>Quantidade</th>
            <th>Usada</th>
            <th>Ações</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="res in reservations" :key="res.id">
            <td>{{ res.restaurant.name }}</td>
            <td>{{ res.date }}</td>
            <td>{{ res.menu }}</td>
            <td>{{ res.quantity }}</td>
            <td>{{ res.used ? 'Sim' : 'Não' }}</td>
            <td class="d-flex gap-2">
              <button class="btn btn-sm btn-success" @click="checkInById(res.id)" :disabled="res.used">✅</button>
              <button class="btn btn-sm btn-warning" @click="undoCheckInById(res.id)" :disabled="!res.used">↩️</button>
              <button class="btn btn-sm btn-danger" @click="openDeleteModal(res.id)">🗑️</button>
            </td>
          </tr>
        </tbody>
      </table>

      <button
        class="btn btn-danger w-100 mt-3"
        @click="openGroupDeleteModal"
        v-if="reservations.length > 1"
      >
         Eliminar Reserva Completa
      </button>
    </div>

    <!-- Modal de Confirmação -->
    <div class="modal fade" id="confirmDeleteModal" tabindex="-1" aria-hidden="true">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header bg-danger text-white">
            <h5 class="modal-title">Confirmar Eliminação</h5>
            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Fechar"></button>
          </div>
          <div class="modal-body">
            Tem a certeza que deseja eliminar esta reserva? Esta ação é irreversível.
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
            <button type="button" class="btn btn-danger" @click="confirmDelete">Confirmar</button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal de Confirmação Total -->
    <div class="modal fade" id="confirmGroupDeleteModal" tabindex="-1" aria-hidden="true">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header bg-danger text-white">
            <h5 class="modal-title">Eliminar Reserva Completa</h5>
            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Fechar"></button>
          </div>
          <div class="modal-body">
            Tem a certeza que deseja eliminar todas as reservas associadas a este token?
            Esta ação é irreversível.
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
            <button type="button" class="btn btn-danger" @click="confirmGroupDelete">Confirmar</button>
          </div>
        </div>
      </div>
    </div>


    <!-- Toast Notification -->
    <div v-if="toastMessage" class="toast-container">
      <div class="toast-message">
        {{ toastMessage }}
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'


export default {
  data() {
    return {
      token: '',
      reservations: [],
      errorMessage: '',
      reservationToDeleteId: null,
      toastMessage: ''
    }
  },
  methods: {
    openGroupDeleteModal() {
      const modal = new bootstrap.Modal(document.getElementById("confirmGroupDeleteModal"))
      modal.show()
    },
    async getReservations() {
      this.errorMessage = ''
      try {
        const res = await axios.get(`http://localhost:8080/reservations/${this.token}`)
        this.reservations = []
        this.$nextTick(() => {
          this.reservations = res.data
        })
      } catch (err) {
        this.handleError(err, 'Reserva não encontrada.')
        this.reservations = []
      }
    },
    async checkInById(id) {
      this.errorMessage = ''
      try {
        const res = await axios.post(`http://localhost:8080/reservations/id/${id}/check-in`)
        const prato = res.data.menu
        this.showToast(`Check-in com sucesso no prato "${prato}"`)
        await this.getReservations()
      } catch (err) {
        this.handleError(err, 'Erro ao confirmar a presença.')
      }
    },
    async undoCheckInById(id) {
      this.errorMessage = ''
      try {
        const res = await axios.post(`http://localhost:8080/reservations/id/${id}/uncheck-in`)
        const prato = res.data.menu
        this.showToast(`Check-in anulado com sucesso no prato "${prato}"`)
        await this.getReservations()
      } catch (err) {
        this.handleError(err, 'Erro ao anular o check-in.')
      }
    },
    async deleteGroupByToken() {
      try {
        await axios.post(`http://localhost:8080/reservations/${this.token}/delete-all`)
        this.showToast(" Todas as reservas com este token foram eliminadas.")
        this.reservations = []
      } catch (err) {
        this.handleError(err, "Erro ao eliminar reservas em grupo.")
      }
    },
    openDeleteModal(id) {
      this.reservationToDeleteId = id
      const modal = new bootstrap.Modal(document.getElementById('confirmDeleteModal'))
      modal.show()
    },
    async confirmDelete() {
      try {
        const prato = this.reservations.find(r => r.id === this.reservationToDeleteId)?.menu || 'desconhecido'
        await axios.post(`http://localhost:8080/reservations/id/${this.reservationToDeleteId}/delete`)
        this.showToast(`Reserva com o prato "${prato}" eliminada com sucesso.`)
        this.reservationToDeleteId = null
        await this.getReservations()
        bootstrap.Modal.getInstance(document.getElementById('confirmDeleteModal')).hide()
      } catch (err) {
        this.handleError(err, 'Erro ao eliminar a reserva.')
      }
    },
    
    confirmGroupDelete() {
      axios.post(`http://localhost:8080/reservations/${this.token}/delete-all`)
        .then(() => {
          this.showToast("Todas as reservas com este token foram eliminadas.")
          this.reservations = []
          bootstrap.Modal.getInstance(document.getElementById('confirmGroupDeleteModal')).hide()
        })
        .catch(err => {
          this.handleError(err, "Erro ao eliminar reservas em grupo.")
        });
    },
    showToast(message) {
      this.toastMessage = message
      setTimeout(() => {
        this.toastMessage = ''
      }, 3000)
    },
    handleError(err, fallbackMessage) {
      if (err.response && err.response.status === 404) {
        this.errorMessage = 'Reserva não encontrada.'
      } else if (err.response && err.response.status === 409) {
        this.errorMessage = err.response.data
      } else {
        this.errorMessage = fallbackMessage
      }
    }
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
</style>