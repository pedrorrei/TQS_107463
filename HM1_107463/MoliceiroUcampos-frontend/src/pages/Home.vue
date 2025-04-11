<template>
  <div class="container py-4">
    <h1>Refeições do Campus Moliceiro</h1>
    <p>Reserva a tua refeição com tempo de forma simples e eficaz. Consulta menus e previsão da meteorologia.</p>
    <router-link class="btn btn-primary me-2" to="/book">Fazer Reserva</router-link>
    <router-link class="btn btn-outline-primary" to="/check-reservation">Consultar Reserva</router-link>

    <div class="row mt-5 text-center">
      <div class="col-md-3">
        <h4>
          Reservas em Grupo
          <span class="badge bg-danger ms-1">NEW</span>
        </h4>
        <p>Já disponível: faz reservas para grupos com seleção personalizada de pratos!</p>
        <router-link to="/group-booking" class="btn btn-outline-danger">Reservar Grupo</router-link>
      </div>
      <div class="col-md-3">
        <h4>Visualiza os Menus</h4>
        <p>Consulta os menus das unidades do campus com previsão da meteorologia.</p>
        <router-link to="/menus" class="btn btn-outline-secondary">Aceder</router-link>
      </div>
      <div class="col-md-3">
        <h4>Faça Sua Reserva</h4>
        <p>Garanta sua refeição com um código de confirmação.</p>
        <router-link to="/book" class="btn btn-outline-secondary">Aceder</router-link>
      </div>
      <div class="col-md-3">
        <h4>Consulte Sua Reserva</h4>
        <p>Verifica ou cancela a tua reserva com o token de reserva.</p>
        <router-link to="/check-reservation" class="btn btn-outline-secondary">Aceder</router-link>
      </div>
      
    </div>
  </div>
</template>

<script setup></script>
