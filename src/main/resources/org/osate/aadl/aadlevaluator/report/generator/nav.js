Vue.component( 'app_nav_menu' , {
    template : `
<nav class="navbar is-black" role="navigation" aria-label="main navigation">
    <div class="navbar-brand">
        <a class="navbar-item" href="#">
            <img src="https://www.pngfind.com/pngs/b/468-4686554_performance-icon-png.png" width="28" height="28">
        </a>
    
        <a role="button" class="navbar-burger burger" aria-label="menu" aria-expanded="false" data-target="navbarBasicExample">
        <span aria-hidden="true"></span>
        <span aria-hidden="true"></span>
        <span aria-hidden="true"></span>
        </a>
    </div>
    
    <div id="navbarBasicExample" class="navbar-menu">
        <div class="navbar-start">
            <a @click.prevent="select('filter')" class="navbar-item" :class="{ 'is-active' : selected === 'filter' }">Filter</a>
            <a @click.prevent="select('table')" class="navbar-item" :class="{ 'is-active' : selected === 'table' }">Table</a>
            <a @click.prevent="select('chart')" class="navbar-item" :class="{ 'is-active' : selected === 'chart' }">Chart</a>
        </div>
    </div>
</nav>

    ` ,

    props : [ "filters" , "reports" ] ,

    data : function(){
        return {
            selected : "filter"
        };
    } ,


    methods : {
        select : function ( name ){
            this.selected = name;
            this.$emit( 'select' , name );
        }
    } ,

    mounted : function (){

    }
});