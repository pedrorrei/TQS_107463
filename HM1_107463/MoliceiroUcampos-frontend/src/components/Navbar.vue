<template>
  <nav class="navbar navbar-expand-lg"  navbar-light bg-light>
    <div class="container-fluid">
      <router-link class="navbar-brand fw-bold" to="/">MoliceiroUCampos</router-link>

      <button
        class="navbar-toggler"
        type="button"
        data-bs-toggle="collapse"
        data-bs-target="#navbarNav"
      >
        <span class="navbar-toggler-icon"></span>
      </button>

      <div class="collapse navbar-collapse" id="navbarNav">
        <ul class="navbar-nav ms-auto">
          <li class="nav-item">
            <router-link class="nav-link" to="/">Início</router-link>
          </li>
          <li class="nav-item">
            <router-link class="nav-link" to="/book">Reservar</router-link>
          </li>
          <li class="nav-item">
            <router-link class="nav-link" to="/group-booking">Reservas de Grupo</router-link>
          </li>
          <li class="nav-item">
            <router-link class="nav-link" to="/check-reservation">Consultar Reserva</router-link>
          </li>
          <li class="nav-item">
            <router-link class="nav-link" to="/staff-verification">Verificação (Staff)</router-link>
          </li>
          <li class="nav-item">
            <router-link class="nav-link" to="/monitoring">Monitoramento</router-link>
          </li>
        </ul>
      </div>
    </div>
  </nav>
</template>
