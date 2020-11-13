Vue.component( 'app_menu' , {
    template : `
<aside class="menu">
  <p class="menu-label">
    Menu
  </p>
  <ul class="menu-list">
    <li>
        <a @click.prevent="select('filter')" :class="{ 'is-active' : selected === 'filter' }">Filter</a>
    </li>
    <li>
        <a @click.prevent="select('table')" :class="{ 'is-active' : selected === 'table' }">Table</a>
    </li>
    <li>
        <a @click.prevent="select('chart')" :class="{ 'is-active' : selected === 'chart' }">Chart</a>
    </li>
  </ul>
</aside>
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