Vue.component( 'chart_bar' , {
    template : `<canvas :id='id'></canvas>` ,

    props : [ "reports" , "attribute" ] ,

    data : function (){
        return {
            id : "chart_bar_" + Math.random() ,
            chart : null
        };
    } ,

    methods : {
        init : function (){
            console.log( "attribute: " + this.attribute );

            if( this.chart ) {
                this.chart.destroy();
            }

            let data = {
                labels : [ "evolutions" ] ,
                datasets: this.getReportDataset()
            };

            let limitMin = this.getLimitDataset( true  );
            let limitMax = this.getLimitDataset( false );

            if( limitMin ) data.datasets.push( limitMin );
            if( limitMax ) data.datasets.push( limitMax );

            let obj = getResume( this.attribute );
            let ctx  = document.getElementById( this.id ).getContext( '2d' );

            if( !isSet( obj.unit ) ) obj.unit = '';
            
            this.chart = new Chart( ctx , {
                type : 'bar' ,
                data : data ,
                options: {
                    legend: {
                        display: false
                    } ,

                    scales: {
                        yAxes: [{
                            scaleLabel: {
                                display: true ,
                                labelString: this.attribute
                            } ,
                            
                            ticks: {
                                callback: function(value, index, values) {
                                    return `${value.toFixed(2)} ${obj.unit}`;
                                }
                            }
                        }]
                    },

                    tooltips: {
                        callbacks: {
                            label: ( tooltipItem ) => {
                                var name = data.datasets[ tooltipItem.datasetIndex ].label;

                                return [ 
                                    `name: ${name}` , 
                                    `${this.attribute}: ${tooltipItem.yLabel} ${obj.unit}`
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
                    type : 'bar' ,
                    label: e.name ,
                    data: [ getReport( e , this.attribute ) ]
                });
            });
    
            return datasets;
        } ,

        getLimitDataset : function ( isLimitMin ) {
            let obj = getResume( this.attribute );
            
            if( (isLimitMin && !isSet( obj.limitMin ) )
                || (!isLimitMin && !isSet( obj.limitMax ) ) )
            {
                return null;
            }
            
            let data = [];

            this.reports.forEach( e => {
                data.push( isLimitMin ? obj.limitMin : obj.limitMax );
            });

            return {
                borderWidth: 1 ,
                pointBackgroundColor: '#DC143C',
                pointBorderColor: '#8B0000',
                borderColor: '#8B0000' ,
                type : 'line' ,
                pointRadius: 5 ,
                pointHoverRadius: 5 ,
                fill: false ,
                tension: 0 ,
                showLine: true ,
                label: isLimitMin ? 'Limit Min' : 'Limit Max' ,
                data  : data
            };
        }
    } ,

    // ------------------------------------ //
    // ------------------------------------ // EVENTS
    // ------------------------------------ //

    mounted : function (){
        this.init();
        console.log( "chart - bar - report: " + this.reports.length );
    } ,

    destroyed: function (){
        if( this.chart ) {
            this.chart.destroy();
        }
    }
} );