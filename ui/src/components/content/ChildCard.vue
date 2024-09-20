<template>
    <div class="row card-group mb-2">
        <router-link
            :to="{path: '/' + item.path}"
            class="col-12 col-md-6 mb-lg-4 mb-2"
            v-for="item in navigation"
            :key="item.path"
        >
            <div class="card">
                <div class="card-body d-flex">
                    <span class="card-icon">
                        <img
                            :src="$store.getters['doc/resourceUrl'](item.icon)"
                            :alt="item.title"
                            width="50px"
                            height="50px"
                        >
                    </span>
                    <div>
                        <h4 class="card-title">
                            {{ item.title }}
                        </h4>
                        <p class="card-text">
                            {{ item.description }}
                        </p>
                    </div>
                </div>
            </div>
        </router-link>
    </div>
</template>

<script setup>
    import {useRouter} from "vue-router";
    import {useStore} from "vuex";

    const router = useRouter();
    const store = useStore();

    const props = defineProps({
        pageUrl: {
            type: String,
            default: undefined
        }
    });

    let currentPage = null;

    if (props.pageUrl) {
        currentPage = props.pageUrl;
    } else {
        currentPage = router.currentRoute.value.params.path;
    }

    currentPage = currentPage.replace(/^\/?(.*?)\/?$/, "$1");

    const resourcesWithMetadata = await store.dispatch("doc/children", currentPage);
    let parentMetadata;
    if (props.pageUrl) {
        parentMetadata = resourcesWithMetadata[currentPage];
    }

    const parentLevel = currentPage.split("/").length;
    const navigation = Object.entries(resourcesWithMetadata)
        .filter(([path]) => path.split("/").length === parentLevel + 1)
        .filter(([path]) => path !== currentPage)
        .map(([path, metadata]) => ({
            path,
            ...parentMetadata,
            ...metadata
        }));
    console.log("NAV : "+JSON.stringify(navigation));
</script>

<style lang="scss" scoped>
    @import "@kestra-io/ui-libs/src/scss/variables";

    .card-title {
        font-size: $font-size-xl !important;
        line-height: 1.375rem !important;
    }

    .card-text {
        font-size: $font-size-sm !important;
        line-height: 1rem !important;
    }

    .card-icon {
        img {
            max-width: unset;
            width: 48px !important;
            height: 48px !important;
        }
    }
</style>
