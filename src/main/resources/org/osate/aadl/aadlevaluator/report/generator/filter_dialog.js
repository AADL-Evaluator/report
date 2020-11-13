Vue.component( 'filter_dialog' , {
    template : `<div class="modal is-active">
  <div class="modal-background"></div>
  <div class="modal-card">
    <header class="modal-card-head">
      <p class="modal-card-title">Filter</p>
      <button class="delete" aria-label="close" @click="closed"></button>
    </header>

    <section class="modal-card-body">

        <div class="field is-horizontal">
            <div class="field-label is-small">
                <label class="label">Factor</label>
            </div>
            <div class="field-body">
                <div class="field">
                    <div class="control">
                        <select class="select" v-model="selected">
                            <optgroup v-for="(value, name) in groups" :key="name" :label="name">
                                <option v-for="item in value" 
                                    :key="item.value" 
                                    :value="item.value">{{ item.title }}</option>
                            </optgroup>
                        </select>
                    </div>
                </div>
            </div>
        </div>

        <div v-if="selected !== ALL_IN_LIMIT">
            <div class="field is-horizontal">
                <div class="field-label is-small">
                    <label class="label">Value Min</label>
                </div>
                <div class="field-body">
                    <div class="field has-addons">
                        <div class="control">
                            <input v-model="min" class="input is-small" style="text-align: right;" type="text" placeholder="0">
                        </div>
                        <div class="control">
                            <a v-if="info && info.unit" class="button is-static is-small">{{ info.unit }}</a>
                            <a v-else class="button is-static is-small"></a>
                        </div>
                    </div>

                    <p class="help">
                        <div v-if="info" class="field is-grouped is-grouped-multiline">
                            <div class="control">
                                <div class="tags has-addons">
                                    <span class="tag is-dark">Value</span>
                                    <span class="tag is-info">{{ info.valueMin }}</span>
                                </div>
                            </div>

                            <div class="control">
                                <div class="tags has-addons">
                                    <span class="tag is-dark">Limit</span>
                                    <span class="tag is-danger">{{ info.limitMin }}</span>
                                </div>
                            </div>
                        </div>
                    </p>
                </div>
            </div>

            <div class="field is-horizontal">
                <div class="field-label is-small">
                    <label class="label">Value Max</label>
                </div>
                <div class="field-body">
                    <div class="field has-addons">
                        <div class="control">
                            <input v-model="max" class="input is-small" style="text-align: right;" type="text" placeholder="10">
                        </div>
                        <div class="control">
                            <a v-if="info && info.unit" class="button is-static is-small">{{ info.unit }}</a>
                            <a v-else class="button is-static is-small"></a>
                        </div>
                    </div>

                    <p class="help">
                        <div v-if="info" class="field is-grouped is-grouped-multiline">
                            <div class="control">
                                <div class="tags has-addons">
                                    <span class="tag is-dark">Value</span>
                                    <span class="tag is-info">{{ info.valueMax }}</span>
                                </div>
                            </div>

                            <div class="control">
                                <div class="tags has-addons">
                                    <span class="tag is-dark">Limit</span>
                                    <span class="tag is-danger">{{ info.limitMax }}</span>
                                </div>
                            </div>
                        </div>
                    </p>
                </div>
            </div>
        </div>

    </section>

    <footer class="modal-card-foot">
        <button class="button is-success" @click="saved">Save</button>
        <button class="button" @click="closed">Cancel</button>
        <button v-if="edit.index >= 0" class="button is-danger" @click="removed">Deleted</button>
    </footer>
  </div>
</div>
    ` ,

    props : [ "edit" ] ,

    data : function (){
        return {
            groups : getItemsGroup() ,
            selected : "" ,
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
        }
    } ,

    methods : {
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

    mounted : function (){
        this.selected = this.edit.factor;
        this.min = this.edit.min;
        this.max = this.edit.max;
    } ,

    destroyed : function (){
        
    }
} );