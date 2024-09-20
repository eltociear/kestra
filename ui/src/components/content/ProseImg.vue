<template>
    <span class="text-center d-block img-block">
        <img
            :src="src"
            :alt="alt"
            :width="width"
            :height="height"
            :class="classWithZoom"
            loading="lazy"
        >
    </span>
</template>

<script setup lang="ts">

    import {computed} from "vue";

    const props = defineProps({
        src: {
            type: String,
            default: ""
        },
        alt: {
            type: String,
            default: ""
        },
        width: {
            type: [String, Number],
            default: undefined
        },
        height: {
            type: [String, Number],
            default: undefined
        },
        class: {
            type: String,
            default: ""
        }
    })

    const classWithZoom = computed(() => {
        return "zoom " + props.class
    })
</script>