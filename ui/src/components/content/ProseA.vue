<template>
    <router-link :to="{path: parsedUrl}">
        <slot />
    </router-link>
</template>
<script>
    export default {
        props: {
            href: {
                type: String,
                default: ""
            },
            target: {
                type: String,
                default: undefined,
                required: false
            }
        },
        computed: {
            parsedUrl() {
                return this.href.replaceAll(/\/\d+\.([^/]*)$/g, "$1");
            }
        }
    }
</script>
