const ITEM_SEPARATOR = "|";
const ALL_IN_LIMIT = 'ALL_IN_LIMIT';

function getFactors(){
    var factors = [];

    for( i in resume ){
        if( resume.hasOwnProperty( i ) ){
            factors.push( i );
        }
    }

    return factors;
}

function getItems(){
    let items = [];

    for( i in resume )
    {
        if( !resume.hasOwnProperty( i ) )
        {
            continue ;
        }

        items.push( {
            value : i ,
            title : i ,
            enabled : isSet( resume[ i ].limitMax ) ,
            valueMin : resume[ i ].valueMin ,
            valueMax : resume[ i ].valueMax ,
            limitMin : resume[ i ].limitMin ,
            limitMax : resume[ i ].limitMax ,
            lessIsBetter : resume[ i ].lessIsBetter ,
        } );

        if( i === 'functionality' 
            || i === 'maintainability' 
            || i === 'performance' )
        {
            for( i2 in resume[ i ] )
            {
                if( !resume[ i ].hasOwnProperty( i2 ) )
                {
                    continue ;
                }

                items.push( {
                    value : `${i}${ITEM_SEPARATOR}${i2}` ,
                    title : i2 ,
                    enabled : true ,
                    valueMin : resume[ i ][ i2 ].valueMin ,
                    valueMax : resume[ i ][ i2 ].valueMax ,
                    limitMin : resume[ i ][ i2 ].limitMin ,
                    limitMax : resume[ i ][ i2 ].limitMax ,
                    lessIsBetter : resume[ i ][ i2 ].lessIsBetter
                } );
            }
        }
    }

    return items;
}

function getItemsGroup( isToIncludeAllInLimit ){
    let items = {
        general : []
    };

    if( !isSet( isToIncludeAllInLimit ) || isToIncludeAllInLimit )
    {
        items.general.push({ 
            value : ALL_IN_LIMIT , 
            title : "All reports that respect all limits"
        });
    }

    for( i in resume )
    {
        if( !resume.hasOwnProperty( i ) )
        {
            continue ;
        }

        if( isSet( resume[ i ].limitMax ) )
        {
            items.general.push( {
                value : i ,
                title : i ,
                unit  : '' ,
                valueMin : resume[ i ].valueMin ,
                valueMax : resume[ i ].valueMax ,
                limitMin : resume[ i ].limitMin ,
                limitMax : resume[ i ].limitMax ,
                lessIsBetter : resume[ i ].lessIsBetter
            } );
        }
        else if( i === 'functionality' 
            || i === 'maintainability' 
            || i === 'performance' )
        {
            if( !isSet( items[ i ] ) )
            {
                items[ i ] = [];
            }

            for( i2 in resume[ i ] )
            {
                if( !resume[ i ].hasOwnProperty( i2 ) )
                {
                    continue ;
                }

                items[ i ].push( {
                    value : `${i}${ITEM_SEPARATOR}${i2}` ,
                    title : i2 ,
                    unit  : resume[ i ][ i2 ].unit ,
                    valueMin : resume[ i ][ i2 ].valueMin ,
                    valueMax : resume[ i ][ i2 ].valueMax ,
                    limitMin : resume[ i ][ i2 ].limitMin ,
                    limitMax : resume[ i ][ i2 ].limitMax ,
                    lessIsBetter : resume[ i ][ i2 ].lessIsBetter ,
                } );
            }
        }
    }

    return items;
}

function isSet( value ){
    return typeof value !== 'undefined' 
        && value !== null;
}

function getResume( name )
{
    if( name.startsWith( "general|" ) )
    {
        name = name.substring( name.indexOf( "|" )  + 1 );
    }

    if( name.includes( ITEM_SEPARATOR ) )
    {
        let parts = name.split( ITEM_SEPARATOR );
        return resume[ parts[0] ][ parts[1] ];
    }
    else
    {
        return resume[ name ];
    }
}

function getReport( report , name )
{
    if( name.startsWith( "general|" ) )
    {
        name = name.substring( name.indexOf( "|" )  + 1 );
    }

    if( name.includes( ITEM_SEPARATOR ) )
    {
        let parts = name.split( ITEM_SEPARATOR );
        return report.characteristics[ parts[0] ][ parts[1] ];
    }
    else
    {
        return report[ name ];
    }
}

function getReportsByFilters( filters )
{
    return reports.filter( report => isReportAccepted( filters , report ) );
}

function isReportAccepted( filters , report )
{
    return filters.findIndex( filter => !isReportAcceptedByFilter( filter , report ) ) === -1;
}

function isReportAcceptedByFilter( filter , report )
{
    if( filter.factor.includes( ITEM_SEPARATOR ) )
    {
        let parts = filter.factor.split( ITEM_SEPARATOR );
        let value = report.characteristics[ parts[0] ][ parts[1] ];

        return filter.min <= value && value <= filter.max;
    }
    else if( filter.factor === ALL_IN_LIMIT )
    {
        return isAllInLimit( report );
    }
    else
    {
        // rank and factor
        let value = report[ filter.factor ];
        return filter.min <= value && value <= filter.max;
    }
}

function isAllInLimit( report )
{
    for( name1 in report.characteristics )
    {
        if( !report.characteristics.hasOwnProperty( name1 ) )
        {
            continue ;
        }

        let group = report.characteristics[ name1 ];

        for( name2 in group )
        {
            if( !group.hasOwnProperty( name2 ) )
            {
                continue ;
            }

            let limitMin = resume[ name1 ][ name2 ].limitMin;
            let limitMax = resume[ name1 ][ name2 ].limitMax;
            let value = group[ name2 ];

            if( (value < limitMin || value > limitMax)
                && limitMin !== 0 
                && limitMax !== 0 
                && isSet( limitMin ) 
                && isSet( limitMax ) 
            )
            {
                return false;
            }
        }
    }

    return true;
}

/**
 * check the value is in limit
 * 
 * @param {object|string} resume    object resume or attribute name
 * @param {number} value            report value
 */
function isInLimit( resume , value )
{
    let l = isSet( resume.limitMin ) 
        ? resume 
        : getResume( attribute );

    return l.limitMin <= value 
        && value <= l.limitMax;
}

function howIsTheBest( reportsSelected , attribute )
{
    let l = getResume( attribute );

    if( !isSet( l.lessIsBetter ) )
    {
        l.lessIsBetter = true;
    }

    let actualValue = l.lessIsBetter 
        ? Number.POSITIVE_INFINITY 
        : Number.NEGATIVE_INFINITY;

    let best = [];
    
    reportsSelected.forEach( report => {
        let reportValue = getReport( report , attribute );

        // -------- it should be respect the limits
        if( reportValue < l.limitMin || reportValue > l.limitMax ){
            return ;
        }
        // -------- it is the best result, clear and put it
        else if( ( l.lessIsBetter && reportValue < actualValue ) 
            || !l.lessIsBetter && reportValue > actualValue ){
            best = [ report.name ];
            actualValue = reportValue;
        }
        // -------- it is the best result, clear and put it
        else if( reportValue === actualValue ){
            best.push( report.name );
        }
    });
    
    return best;
}




























// -------------------------------------- //
// -------------------------------------- // CONSTANTS
// -------------------------------------- //

var resume  = {RESUME};
var reports = [ {REPORTS} ];