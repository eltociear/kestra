<template>
    <top-nav-bar :title="routeInfo.title" :breadcrumb="routeInfo.breadcrumb" />
    <Suspense>
        <MDCRenderer v-if="ast?.body" :body="ast.body" :data="ast.data" :components="{a:'prose-a',img:'prose-img'}" />
    </Suspense>
</template>

<script>
    import useMarkdownParser from "@kestra-io/ui-libs/src/composables/useMarkdownParser";
    import MDCRenderer from "@kestra-io/ui-libs/src/components/content/MDCRenderer.vue";
    import TopNavBar from "../layout/TopNavBar.vue";
    import {mapGetters} from "vuex";

    const parse = useMarkdownParser();

    export default {
        computed: {
            ...mapGetters("doc", ["pageMetadata"]),
            path() {
                let path = this.$route.params.path;
                if (path === "") {
                    return undefined;
                }
                return path;
            },
            pathParts() {
                return this.path?.split("/") ?? [];
            },
            routeInfo() {
                return {
                    title: this.pageMetadata?.title ?? this.$t("docs"),
                    breadcrumb: [
                        {
                            label: this.$t("docs"),
                            link: {
                                name: "docs/view"
                            }
                        },
                        ...(this.pathParts.map((part, index) => {
                            return {
                                label: part,
                                link: {
                                    name: "docs/view",
                                    params: {
                                        path: this.pathParts.slice(0, index + 1).join("/")
                                    }
                                }
                            }
                        }))
                    ]
                };
            }
        },
        components: {TopNavBar, MDCRenderer},
        data() {
            return {
                ast: undefined,
            };
        },
        watch: {
            "$route.params.path": {
                async handler() {
                    const response = await this.$store.dispatch("doc/fetchResource", `docs${this.path === undefined ? "" : `/${this.path}`}`);
                    await this.$store.commit("doc/setPageMetadata", response.metadata);
                    this.ast = await parse(response.content);
                },
                immediate: true
            }
        }
    };
</script>

<style lang="scss" scoped>

</style>