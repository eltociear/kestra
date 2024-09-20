import path from "path";
import {defineConfig} from "vite";
import vue from "@vitejs/plugin-vue";
import {visualizer} from "rollup-plugin-visualizer";
import eslintPlugin from "vite-plugin-eslint";
import * as sass from "sass"

import {filename} from "./plugins/filename"
import {commit} from "./plugins/commit"

export default defineConfig({
    base: "",
    build: {
        outDir: "../webserver/src/main/resources/ui",
    },
    resolve: {
        alias: {
            "override": path.resolve(__dirname, "src/override/"),
            // allow to render at runtime
            vue: "vue/dist/vue.esm-bundler.js",

            "#mdc-imports": path.resolve(__dirname, "node_modules/@kestra-io/ui-libs/stub-mdc-imports.js"),
            "#mdc-configs": path.resolve(__dirname, "node_modules/@kestra-io/ui-libs/stub-mdc-imports.js"),
        },
    },
    plugins: [
        vue(),
        visualizer(),
        eslintPlugin({failOnWarning: true, failOnError: true}),
        filename(),
        commit()
    ],
    assetsInclude: ["**/*.md"],
    css: {
        devSourcemap: true,
        preprocessorOptions: {
            scss: {
                logger: sass.Logger.silent
            },
        }
    },
    optimizeDeps: {
        include: [
            "lodash"
        ],
        exclude: [
            "* > @kestra-io/ui-libs"
        ]
    },
})
