Vue.component( 'chart_scatter' , {
    template : `<canvas :id='id'></canvas>` ,

    props : [ "reports" , "x" , "y" ] ,

    data : function (){
        return {
            id : "chart_scatter_" + Math.random() ,
            chart : null
        };
    } ,

    methods : {
        init : function (){
            if( this.chart ) {
                this.chart.destroy();
            }

            let data = {
                datasets: this.getReportDataset()
            };

            data.datasets.push( this.getLimitDataset() );

            let x = this.x;
            let y = this.y;

            let xObj = getResume( this.x );
		    let yObj = getResume( this.y );
            let ctx  = document.getElementById( this.id ).getContext( '2d' );

            if( !isSet( xObj.unit ) ) xObj.unit = '';
            if( !isSet( yObj.unit ) ) yObj.unit = '';

            this.chart = Chart.Scatter( ctx , {
                data : data ,
                options: {
                    legend: {
                        display: false
                    } ,

                    scales: {
                        xAxes: [{
                            scaleLabel: {
                                display: true ,
                                labelString: this.x
                            },

                            ticks: {
                                callback: function(value, index, values) {
                                    return `${value.toFixed(2)} ${xObj.unit}`;
                                }
                            }
                        }],

                        yAxes: [{
                            scaleLabel: {
                                display: true ,
                                labelString: this.y
                            },
                            
                            ticks: {
                                callback: function(value, index, values) {
                                    return `${value.toFixed(2)} ${yObj.unit}`;
                                }
                            }
                        }]
                    },

                    tooltips: {
                        callbacks: {
                            label: function (tooltipItem) {
                                var name = data.datasets[ tooltipItem.datasetIndex ].label;
                                return [ 
                                    `name: ${name}` , 
                                    `${x}: ${tooltipItem.xLabel} ${xObj.unit}` , 
                                    `${y}: ${tooltipItem.yLabel} ${yObj.unit}`
                                ];
                            }
                        }
                    } ,

                    title: {
                        display: true,
                        text: 'Chart Report'
                    } ,
                }
            });
        } ,

        getReportDataset : function() {
            let datasets   = [];
            
            this.reports.forEach(e => {
                datasets.push({
                    label: e.name ,
                    //borderColor: "#000000" ,
                    //backgroundColor: "#112233" ,
                    data: [{
                        x: getReport( e , this.x ) ,
                        y: getReport( e , this.y )
                    }]
                });
            });
    
            return datasets;
        } ,

        getLimitDataset : function () {
            let xObj = getResume( this.x );
            let yObj = getResume( this.y );
            
            return {
                borderWidth: 1 ,
                //pointBackgroundColor: '#DC143C',
                //pointBorderColor: '#8B0000',
                //borderColor: '#8B0000' ,
                pointRadius: 5 ,
                pointHoverRadius: 5 ,
                fill: true ,
                tension: 0 ,
                showLine: true ,
                label: 'limit' ,
                data  : [
                    { x : xObj.limitMin , y : yObj.limitMin } ,
                    { x : xObj.limitMin , y : yObj.limitMax } ,
                    { x : xObj.limitMax , y : yObj.limitMax } ,
                    { x : xObj.limitMax , y : yObj.limitMin } ,
                    { x : xObj.limitMin , y : yObj.limitMin }
                ]
            };
        }
    } ,

    watch : {

    } ,

    // ------------------------------------ //
    // ------------------------------------ // EVENTS
    // ------------------------------------ //

    mounted : function (){
        this.init();
        console.log( "chart - scatter - report: " + this.reports.length );
    } ,

    destroyed: function (){
        if( this.chart ) {
            this.chart.destroy();
        }
    }
} );