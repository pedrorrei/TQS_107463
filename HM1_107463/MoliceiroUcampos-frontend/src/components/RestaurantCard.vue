<template>
  <div class="col-md-4 mb-4">
    <div
      class="card h-100 shadow-sm"
      :class="{ 'border-success bg-light': isSelected }"
      @click="selectRestaurant"
    >
      <div class="card-body">
        <h5 class="card-title">{{ restaurant.name }}</h5>
            </div>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    restaurant: Object,
    isSelected: Boolean
  },
  methods: {
    selectRestaurant() {
      this.$emit('select', this.restaurant)
    }
  }
}
</script>

<style scoped>
.card {
  cursor: pointer;
  transition: transform 0.2s ease-in-out;
}
.card:hover {
  transform: scale(1.03);
}
.border-success {
  border: 2px solid #28a745 !important;
}
</style>
