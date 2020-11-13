Vue.component( 'chart_list' , {
    template : `
    <div>
        <div class="columns">
            <div class="column is-four-fifths">
                <h3 class="title is-3">Charts</h3>
            </div>
            <div class="column">
                <button class="button is-primary" @click="add">add</button>
            </div>
        </div>

        <div>
            <chart v-for="(chart,index) in charts" 
                :key="chart" 
                :reports="reports"
                @removed="less( index )"
            ></chart>
        </div>
    </div>
    ` ,

    props : [ "reports" ] ,

    data : function () {
        return {
            charts : [ "chart_" + Math.random() ]
        };
    } ,

    methods : {
        add : function (){
            this.charts.push( "chart_" + Math.random() );
        } ,

        less : function ( index ){
            if( this.charts.length > 0 ){
                this.charts.splice( index , 1 );
            }
        }
    }
});


Vue.component( 'chart' , {
    template : `
<div class="container is-fullhd mb-6">
    <div class="columns">

        <div class="column is-one-third">
            <div class="field">
                <label class="label is-small">X Axis:</label>
                <div class="control">
                    <div class="select is-small">
                        <select id="X_axis" name="x" v-model="x">
                            <optgroup v-for="(value, name) in groups" :key="name" :label="name">
                                <option v-for="item in value" 
                                    :key="item.value" 
                                    :value="item.value">{{ item.title }}</option>
                            </optgroup>
                        </select>
                    </div>
                </div>
            </div>

            <div class="field">
                <label class="label is-small">Y Axis:</label>
                <div class="control">
                    <div class="select is-small">
                        <select id="Y_axis" name="y" v-model="y">
                            <optgroup v-for="(value, name) in groups" :key="name" :label="name">
                                <option v-for="item in value" 
                                    :key="item.value" 
                                    :value="item.value">{{ item.title }}</option>
                            </optgroup>
                        </select>
                    </div>
                </div>
            </div>

            <button class="button is-primary" @click.prevent="generate">Generate</button>
            <button class="button is-danger" @click.prevent="$emit('removed')">Remove</button>
        </div>

        <div class="column">
            <chart_scatter v-if="created" :reports="reports" :x="x" :y="y"></chart_scatter>
        </div>
    </div>
</div>
    ` ,

    props : [ "reports" ] ,

    data : function (){
        return {
            groups : getItemsGroup( false ) ,
            x : "" ,
            y : "" ,
            created : false
        };
    } ,

    methods : {
        
        generate : function (){
            this.created = false;

            setTimeout( () => {
                this.created = this.x !== "" && this.y !== "";
            } , 100 );
        }
    } ,

    mounted : function (){
        console.log( "reports: " + this.reports.length );
    }
} );