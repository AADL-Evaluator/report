Vue.component( 'table_list' , {
    template : `
    <div>
        <div class="columns">
            <div class="column is-four-fifths">
                <h3 class="title is-3">Reports ({{ reports.length }})</h3>
            </div>

            <div class="column">
            </div>
        </div>

        <table_01 v-if="show" 
            :columns="getColumns()" 
            :rows="getRows()"
            @selected="selected = $event"
        ></table_01>
        <div v-else>Loading...</div>

        <bar_modal 
            v-if="selected" 
            :reports="reports" 
            :attribute="selected" 
            @closed="selected = null"
        />
    </div>
    ` ,

    props : [ "reports" ] ,

    data : function () {
        return {
            groups : getItemsGroup( false ) ,
            charts : [ "chart_" + Math.random() ] ,
            show : false ,
            selected : null
        };
    } ,

    methods : {
        getReport : function( report , name ){
            return getReport( report , name );
        } ,

        getColumns : function (){
            let columns = [ {
                label : "attribute" ,
                field : "attribute" ,
                type  : 'text' ,
                width : '400px' ,
            }];

            this.reports.forEach( report => {
                columns.push( {
                    label : report.name ,
                    field : report.name ,
                    type  : 'number' ,
                    width : '150px'  ,
                });
            });

            return columns;
        } ,

        getRows : function (){
            let rows = [];
            let groups = getItemsGroup( false );

            for( groupName in groups )
            {
                if( !groups.hasOwnProperty( groupName ) ){
                    continue ;
                }

                let group = groups[ groupName ];

                rows.push( {
                    label : groupName ,
                    items : group ,
                    children: this.getRowsChildren( group )
                });
            }

            return rows;
        } ,

        getRowsChildren : function( items ){
            let rows = [];

            items.forEach( item => {
                let row = {
                    attribute : item.title
                };

                this.reports.forEach( report => {
                    row[ report.name ] = this.getReport( report , item.value );
                } );
                
                rows.push( row );
            } );

            return rows;
        }
    } ,

    mounted : function (){
        setTimeout( () => this.show = true , 100 );
    }
});



Vue.component( 'table_01' , {
    template : `
    <table class="table is-bordered is-striped is-narrow is-hoverable is-fullwidth">
        <thead>
            <tr class="header_move">
                <th v-for="(column,index) in columns" 
                    :key="index" 
                    class="is-dark p-3" 
                    :style="getWidthStyle( column.width )"
                >
                    {{ column.label }}
                </th>
            </tr>
        </thead>

        <tbody v-for="group in rows" :key="group.label">
            <tr>
                <th :colspan="columns.length" 
                    class="p-3"
                >{{ group.label }}</th>
            <tr>

            <tr v-for="(child,i1) in group.children" 
                :key="child.attribute" 
                @dblclick="selected( group.label , child.attribute )"
            >
                <td v-for="(row,i2) in child" 
                    :key="i2"
                    :style="getRowStyle( i2 )"
                    :title="child.attribute"
                >{{ getValue( row , group.items[ i1 ] ) }}</td>
            </tr>
        </tbody>
    </table>
    ` ,

    props : [ "columns" , "rows" ] ,

    methods : {
        getRowStyle : function ( index ){
            return index == 'attribute'
                ? "text-indent: 2em;"
                : "text-align: right;";
        } ,

        getWidthStyle : function ( width ){
            return `min-width: ${width};`;
        } ,
        
        getValue : function ( value , resume ){
            if( !isSet( value ) )
            {
                return '';
            }
            else if( typeof value === 'number' )
            {
                return (value.toFixed(4) + " " + resume.unit).trim();
            }
            else
            {
                return value;
            }
        } ,

        selected : function ( group , attribute ){
            this.$emit( "selected" , `${group}${ITEM_SEPARATOR}${attribute}` );
        }
    } ,

    mounted : function (){
        setTimeout( () => {
            let elements = document.querySelectorAll( 'table' );
            stickyThead.apply( elements );
        } , 100 );
    } ,

    beforeDestroy : function (){
        let elements = document.querySelectorAll( 'table' );
        stickyThead.apply( elements , 'destroy' );
    } ,

} );


Vue.component( 'bar_modal' , {
    template : `
    <div class="modal is-active">
        <div class="modal-background"></div>
        <div class="modal-content" style="background: white">
            <chart_bar :reports="reports" :attribute="attribute" />
        </div>
        <button class="modal-close is-large" aria-label="close" @click="$emit('closed')"></button>
    </div>
    ` ,

    props : [ "reports" , "attribute" ] ,

    methods : {

    } ,

    mounted : function (){
        
    }

} );