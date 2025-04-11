<template>
  <div class="container mt-4">
    <h2>Consultar Reserva</h2>

    <div class="mb-3">
      <input
        v-model="token"
        type="text"
        class="form-control"
        placeholder="Insere o código da reserva"
      />
    </div>

    <button class="btn btn-primary" @click="checkReservation">Consultar</button>

    <div v-if="errorMessage" class="alert alert-danger mt-3">
      {{ errorMessage }}
    </div>

    <div v-if="reservations.length" class="mt-4">
      <h5>Detalhes da Reserva</h5>
      <table class="table">
        <thead>
          <tr>
            <th>Restaurante</th>
            <th>Data</th>
            <th>Prato</th>
            <th>Quantidade</th>
            <th>Usada</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="res in reservations" :key="res.id">
            <td>{{ res.restaurant.name }}</td>
            <td>{{ res.date }}</td>
            <td>{{ res.menu }}</td>
            <td>{{ res.quantity }}</td>
            <td>{{ res.used ? 'Sim' : 'Não' }}</td>
          </tr>
        </tbody>
      </table>

      <button
        class="btn btn-danger mt-3"
        data-bs-toggle="modal"
        data-bs-target="#confirmDeleteModal"
      >
       Cancelar Reserva
      </button>
    </div>

    <!-- Modal de Confirmação -->
    <div class="modal fade" id="confirmDeleteModal" tabindex="-1" aria-hidden="true" data-bs-backdrop="static" data-bs-keyboard="false">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header bg-danger text-white">
            <h5 class="modal-title">Confirmar Cancelamento</h5>
            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Fechar"></button>
          </div>
          <div class="modal-body">
            Tem a certeza que deseja cancelar esta reserva? Esta ação não pode ser desfeita.
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
            <button type="button" class="btn btn-danger" @click="confirmCancelReservation">Confirmar</button>
          </div>
        </div>
      </div>
    </div>

    <!-- Toast Notification -->
    <div v-if="toastMessage" class="toast-container">
      <div class="toast-message" :class="toastType">
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
      toastMessage: '',
      toastType: 'success'
    }
  },
  methods: {
    async confirmCancelReservation() {
      try {
        // Primeiro realizar a operação de cancelamento
        await this.cancelReservation();
        
        // Depois fechar o modal
        const modalEl = document.getElementById('confirmDeleteModal');
        const modal = bootstrap.Modal.getInstance(modalEl);
        if (modal) {
          modal.hide();
          
          // Garantir que o backdrop seja removido
          setTimeout(() => {
            const backdrop = document.querySelector('.modal-backdrop');
            if (backdrop) {
              backdrop.remove();
            }
            document.body.classList.remove('modal-open');
            document.body.style.removeProperty('padding-right');
          }, 300);
        }
      } catch (error) {
        console.error('Erro ao cancelar reserva:', error);
      }
    },
    async checkReservation() {
      this.reservations = []
      this.errorMessage = ''

      if (!this.token) {
        this.errorMessage = 'Insere um código válido.'
        return
      }

      try {
        const res = await axios.get(`http://localhost:8080/reservations/${this.token}`)
        this.reservations = res.data
      } catch (err) {
        if (err.response && err.response.status === 404) {
          this.errorMessage = 'Reserva não encontrada.'
        } else {
          this.errorMessage = 'Erro ao consultar a reserva.'
        }
      }
    },
    async cancelReservation() {
      try {
        await axios.post(`http://localhost:8080/reservations/${this.token}/delete-all`)
        this.reservations = []
        this.errorMessage = ''
        this.showToast('Reserva cancelada com sucesso.', 'success')
        return true;
      } catch (err) {
        this.showToast('Erro ao cancelar a reserva.', 'error')
        return false;
      }
    },
    showToast(message, type = 'success') {
      this.toastMessage = message
      this.toastType = type
      setTimeout(() => {
        this.toastMessage = ''
      }, 3000)
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
  color: white;
  padding: 12px 20px;
  border-radius: 6px;
  box-shadow: 0px 4px 10px rgba(0,0,0,0.2);
  animation: fadein 0.3s ease, fadeout 0.5s ease 2.5s;
}
.toast-message.success {
  background-color: #28a745;
}
.toast-message.error {
  background-color: #dc3545;
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