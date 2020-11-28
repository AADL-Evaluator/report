Vue.component( 'filter_dialog' , {
    template : `
    <b-modal v-model="mostrar">
        <div class="p-6" style="background: white;">
            <h1 class="title">Filter</h1>

            <div class="columns">
                <div class="column">
                    <b-field label="Attribute">
                        <b-select size="is-small" placeholder="Select the attribute" v-model="selected">
                            <optgroup v-for="(value, name) in groups" :key="name" :label="name">
                                <option v-for="item in value" 
                                    :key="item.value" 
                                    :value="item.value">{{ item.title }}</option>
                            </optgroup>
                        </b-select>
                    </b-field>
                </div>

                <div class="column">
                    <b-field size="is-small" label="Slider Range" v-if="info">
                        <b-select size="is-small" placeholder="Select the limit" v-model="sliderRange">
                            <option value="0">Limit defined</option>
                            <option value="1">Value found</option>
                        </b-select>
                    </b-field>
                </div>

                <div class="column">
                    <b-field size="is-small" label="Min" v-if="info">
                        <b-input size="is-small" 
                            placeholder="min value" 
                            style="text-align: right;"
                            v-model="min"
                            expanded
                        ></b-input>
                        <p class="control">
                            <span class="button is-static is-small">{{ info.unit }}</span>
                        </p>
                    </b-field>
                </div>

                <div class="column">
                    <b-field size="is-small" label="Max" v-if="info">
                        <b-input size="is-small" 
                            placeholder="max value" 
                            style="text-align: right;" 
                            v-model="max"
                            expanded
                        ></b-input>
                        <p class="control">
                            <span class="button is-static is-small">{{ info.unit }}</span>
                        </p>
                    </b-field>
                </div>
            </div>

            <div class="columns">
                <div class="column p-6">
                    <my-range 
                        title="Range"
                        v-if="info"
                        :value_min="min" 
                        :value_max="max"
                        :range="range" 
                        :unit="info.unit" 
                        @input="setValues"
                    />
                </div>
            </div>

            <div class="buttons">
                <b-button @click="saved" type="is-link">Save</b-button>
                <b-button @click="closed">Cancel</b-button>
                <b-button @click="removed" type="is-danger" v-if="edit.index >= 0">Delete</b-button>
            </div>
        </div>
    </b-modal>
    ` ,

    props : [ "edit" ] ,

    data : function (){
        return {
            mostrar : true ,
            groups : getItemsGroup() ,
            selected : "" ,
            sliderRange : 0 ,
            min : 0 ,
            max : 0 ,
            ALL_IN_LIMIT : ALL_IN_LIMIT
        };
    } ,

    computed : {
        info : function (){
            if( !isSet( this.selected ) ){
                return {
                    valueMin : 0 ,
                    valueMax : 0 ,
                    limitMin : 0 ,
                    limitMax : 0
                };
            }

            return getResume( this.selected );
        } ,

        range : function (){
            if( !isSet( this.info ) ){
                return { min : 0 , max : 0 };
            }

            return Number( this.sliderRange ) == 0
                ? { min : this.info.limitMin , max : this.info.limitMax }
                : { min : this.info.valueMin , max : this.info.valueMax };
        }
    } ,

    methods : {
        setValues : function ( values ){
            this.min = values[0];
            this.max = values[1];
        } ,

        saved : function(){
            this.$emit( 'saved' , {
                index : this.edit.index ,
                factor : this.selected ,
                min : this.min ,
                max : this.max ,
                unit : isSet( this.info ) && isSet( this.info.unit )
                    ? this.info.unit 
                    : ''
            });
            this.closed();
        } ,

        closed : function (){
            this.$emit( 'closed' );
        } ,

        removed : function (){
            this.$emit( 'removed' , this.edit.index );
            this.closed();
        }
    } ,

    watch : {
        mostrar : function ( v ){
            this.closed();
        }
    } ,

    mounted : function (){
        this.selected = this.edit.factor;
        this.min = this.edit.min;
        this.max = this.edit.max;
    } ,

    destroyed : function (){
        
    }
} );



Vue.component( 'my-range' , {
    template : `
    <b-field :label="title">
        <b-slider 
            :value="value"
            @input="event"
            size="is-small"
            :min="range.min" 
            :max="range.max"
            :custom-formatter="formatter"
            :tooltip="true" 
            :step="step"
            tooltip-always
        ></b-slider>
    </b-field>
    ` ,

    props : [ "title" , "value_min" , "value_max" , "unit" , "range" ] ,

    data : function (){
        return {
            value : [ 0 , 0 ]
        };
    } ,

    watch : {
        value_min : function (){
            this.value = [ 
                this.value_min , 
                this.value_max 
            ];
        } ,

        value_max : function (){
            this.value = [ 
                this.value_min , 
                this.value_max 
            ];
        } ,
    } ,

    computed : {
        step : function (){
            return (this.range.max - this.range.min) / 10;
        }
    } ,

    methods : {
        formatter : function ( value ){
            return this.unit 
                ? `${value.toPrecision(4)} ${this.unit}`
                : `${value}`;
        } ,

        event : function ( value ){
            if( isSet( value ) ){
                this.$emit( 'input' , value );
            }
        }
    } ,

    created : function (){
        this.value = [ 
            this.value_min , 
            this.value_max 
        ];
    }
} );