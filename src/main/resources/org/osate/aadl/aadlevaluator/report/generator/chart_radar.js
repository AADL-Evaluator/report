Vue.component( 'chart_radar' , {
    template : `<canvas :id='id'></canvas>` ,

    props : [ "evolutions" , "axes" ] ,

    data : function (){
        return {
            id : "chart_randar_" + Math.random() ,
            chart : null
        };
    } ,

    computed : {

    } ,

    methods : {
        init : function (){
            if( this.chart ) {
                this.chart.destroy();
            }

            this.chart = null;
        }
    } ,

    watch : {

    } ,

    // ------------------------------------ //
    // ------------------------------------ // EVENTS
    // ------------------------------------ //

    mounted : function (){
        // executado quando é montado
    } ,

    destroyed: function (){
        if( this.chart ) {
            this.chart.destroy();
        }
    }
} );