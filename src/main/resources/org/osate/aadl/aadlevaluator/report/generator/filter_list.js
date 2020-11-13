Vue.component( 'filter_list' , {
    template : `
    <div>
        <div class="columns">
            <div class="column is-four-fifths">
                <h3 class="title is-3">Filters</h3>
            </div>
            <div class="column">
                <button class="button is-primary" @click="add">add</button>
            </div>
        </div>

        <div>
            <table class="table is-fullwidth">
                <thead>
                    <tr>
                        <th>Factor</th>
                        <th>min</th>
                        <th>max</th>
                        <th></th>
                    </tr>
                </thead>

                <tbody>
                    <tr v-if="filters === null || filters.length === 0">
                        <td colspan="4">No filter was defined.</td>
                    </tr>

                    <tr v-else v-for="(filter,index) in filters" :key="filter.factor">
                        <td>{{ filter.factor }}</td>
                        <td style="text-align: right">{{ filter.min }} {{ filter.unit }}</td>
                        <td style="text-align: right">{{ filter.max }} {{ filter.unit }}</td>
                        <td>
                            <button class="button" @click="edit( index , filter)">edit</button>
                            <button class="button is-danger" @click="remove( index )">delete</button>
                        </td>
                    </tr>
                </tbody>

                <tfoot>
                    <tr>
                        <th>Reports</th>
                        <th>Total: {{ total }}</th>
                        <th>Matched: {{ subtotal }}</th>
                        <th></th>
                    </tr>
                </tfoot>
            </table>

            <filter_dialog 
                v-if="filter" 
                :edit="filter"
                @saved="saved" 
                @closed="closed"
                @removed="remove"
            ></filter_dialog>
        </div>
    </div>
    ` ,

    props : [ "filters" , "total" , "subtotal" ] ,

    data : function (){
        return {
            filter : null
        };
    } ,

    methods : {
        add : function (){
            this.filter = { 
                index : -1 , 
                factor : "" , 
                min : 0 , 
                max : 0 , 
                unit : "" 
            };
        } ,

        edit : function ( index , selected ){
            this.filter = {
               index : index ,
               factor : selected.factor ,
               min : selected.min ,
               max : selected.max ,
               unit : selected.unit
            };
        } ,

        remove : function ( index ){
            this.filters.splice( index , 1 );
            this.$emit( 'refresh' , this.filters );
        } ,

        // ---------- // EVENTS
        // ---------- // EVENTS
        // ---------- // EVENTS

        saved : function ( value ){
            let item = value;

            if( item.index === -1 ) {
                delete item.index;
                this.filters.push( item );
            }
            else {
                this.filters[ item.index ] = item;
                delete this.filters[ item.index ].index;
            }
            
            this.$emit( 'refresh' , this.filters );
            this.closed();
        } ,

        closed : function (){
            this.filter = null;
        }
    } ,

    mounted : function (){
        console.log( "Factor list mounted..." );
    }
} );