<template>
    <div class="container-box">
        <div class="main-container">
            <div class="data">
                <h1 class="title">页面不存在</h1>
                <p>抱歉！我们找不到您想访问的页面...</p>
                <button class="button" @click="goBack">
                    返回上一页
                </button>
            </div>

            <div class="error-img">
                <img src="@/assets/404.png" alt="">
                <div class="error-shadow"></div>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    name: 'page404',
    methods: {
        goBack() {
            this.$router.back();
        }
    }
}
</script>

<style lang="less" scoped>
.container-box {
    background-color: rgb(75, 75, 255);
    display: grid;
    padding: 144px 0 32px;
    height: 78.65vh;
    color: white;
    font-size: 15px;
    font-weight: 500;
}

.main-container {
    display: grid;
    align-content: center;
    row-gap: 40px;
    max-width: 992px;
    margin-left: 24px;
    margin-right: 24px;
}

.data {
    text-align: center;
}

.title {
    font-size: 38px;
    margin: 12px 0;
}

.button {
    display: inline-block;
    margin-top: 12px;
    padding: 12px 24px;
    background-color: white;
    color: rgb(75, 75, 255);
    border-radius: 48px;
    transition: 0.4s;
    text-decoration: none;
    border: none;
    cursor: pointer;
    
    &:hover {
        box-shadow: 0 4px 15px rgba(34, 24, 6, 0.2);
        transform: translateY(-2px);
        background-color: #f0f0f0;
    }
    
    &:focus {
        outline: none;
        box-shadow: 0 0 0 3px rgba(255, 255, 255, 0.5);
    }
}

.error-img {
    justify-self: center;

    img {
        width: 270px;
        animation: floaty 1.8s infinite alternate;
    }
}

@keyframes floaty {
    0% {
        transform: translateY(0);
    }
    100% {
        transform: translateY(20px);
    }
}

.error-shadow {
    width: 130px;
    height: 24px;
    background-color: rgba(34, 30, 24, 0.16);
    border-radius: 50%;
    margin: 0 auto;
    filter: blur(7px);
    animation: shadow 1.8s infinite alternate;
}

@keyframes shadow {
    0% {
        transform: scale(1, 1);
    }
    100% {
        transform: scale(0.9, 0.9);
    }
}

@media screen and (min-width: 992px) {
    .main-container {
        grid-template-columns: repeat(2, 1fr);
        align-items: center;
        column-gap: 32px;
    }

    .data {
        text-align: initial;
    }

    .error-img img {
        width: 400px;
    }

    .error-shadow {
        width: 250px;
        height: 40px;
    }
}

@media screen and (min-width: 1024px) {
    .main-container {
        margin-left: auto;
        margin-right: auto;
    }

    .container-box {
        font-size: 16px;
    }

    .title {
        font-size: 60px;
    }
}

@media screen and (min-width: 2048px) {
    .main-container {
        max-width: 1400px;
        column-gap: 64px;
    }
}
</style>