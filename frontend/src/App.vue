<template>
  <div class="app-container">
    <el-container style="height: 100vh;">
      <el-aside width="200px" style="background-color: #304156; padding: 0; margin: 0;">
        <div class="logo">
          <span>设备管理系统</span>
        </div>
        <el-menu
          :default-active="activeMenu"
          class="el-menu-vertical-demo"
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409EFF"
          router
          @select="handleSelect"
        >
          <el-menu-item index="/measurement-ledger">
            <span>计量台账</span>
          </el-menu-item>
          <el-menu-item index="/equipment-ledger">
            <span>设备台账</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-container>
        <el-main style="background-color: #f0f2f5; padding: 0px;">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const activeMenu = ref(route.path || '/measurement-ledger')

const handleSelect = (key) => {
  activeMenu.value = key
}

// 监听路由变化，保持菜单高亮
watch(() => route.path, (newPath) => {
  activeMenu.value = newPath
})
</script>

<style>
/* 全局CSS重置 */
*,
*::before,
*::after {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

/* 重置字体和行高 */
html {
  font-size: 16px;
  line-height: 1.5;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  font-size: 14px;
  color: #303133;
  background-color: #fff;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

/* 重置列表样式 */
ul, ol {
  list-style: none;
}

/* 重置链接样式 */
a {
  color: inherit;
  text-decoration: none;
}

/* 重置表单元素 */
input, button, select, textarea {
  font: inherit;
  color: inherit;
  border: none;
  outline: none;
  background: none;
}

/* 重置按钮 */
button {
  cursor: pointer;
}

/* 重置图片 */
img {
  max-width: 100%;
  height: auto;
  vertical-align: middle;
}

/* 重置表格 */
table {
  border-collapse: collapse;
  border-spacing: 0;
}

/* 确保盒模型一致性 */
html, body, #app {
  width: 100%;
  height: 100%;
}

.app-container {
  width: 100%;
  height: 100vh;
}

.el-aside {
  transition: width 0.28s;
}

.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  background-color: #2b3a4a;
  color: #fff;
  font-size: 16px;
  width: 100%;
  box-sizing: border-box;
}

.el-menu-vertical-demo {
  height: calc(100vh - 60px);
  border-right: none;
  width: 100%;
  box-sizing: border-box;
  padding: 0 !important;
  margin: 0 !important;
  border: none !important;
  min-width: 0 !important;
}

.el-menu-item {
  height: 50px;
  line-height: 50px;
  font-size: 14px;
  padding-left: 20px !important;
  margin: 0;
  width: 100%;
  box-sizing: border-box;
}

.el-menu-item:hover {
  background-color: #263445 !important;
}

.el-header {
  padding: 0 20px;
}

.el-main {
  overflow: auto;
}
</style>