package com.github.onsdigital.babbage.template.handlebars.helpers.resolve;

import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.TagType;
import com.github.jknack.handlebars.Context;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import com.google.gson.JsonParser;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.Before;

import com.github.onsdigital.babbage.configuration.Babbage;
import com.github.onsdigital.babbage.configuration.ApplicationConfiguration;
import com.github.onsdigital.babbage.content.client.ContentClientCache;
import com.github.onsdigital.babbage.content.client.ContentResponse;
import static com.github.onsdigital.babbage.content.client.ContentClient.depth;
import com.github.onsdigital.babbage.util.TaxonomyRenderer;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;

public class DataHelpersTest extends TestCase {
    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
    }
    @Test
    public void testDataHelpers_resolveTaxonomy_nullValues_thrownExcption() throws Exception {

        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            DataHelpers.resolveTaxonomy.apply(null, null);
        });
        assertEquals(java.lang.NullPointerException.class, exception.getClass());

    }

    @Test
    public void testDataHelpers_resolveTaxonomy_nullOption_thrownException() throws Exception {
        Object uri = "/navigation";

        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            DataHelpers.resolveTaxonomy.apply(uri, null);
        });
        assertEquals(java.lang.NullPointerException.class, exception.getClass());

    }
    @Test
    public void testDataHelpers_resolveTaxonomy() throws Exception {

        ApplicationConfiguration mockAC = mock(ApplicationConfiguration.class);
        Babbage mockBabbage = mock(Babbage.class);
        com.github.onsdigital.babbage.util.json.JsonUtil mockJU = mock(com.github.onsdigital.babbage.util.json.JsonUtil.class);
        ContentClientCache mockClient = mock(ContentClientCache.class);
        ContentResponse mockStream = mock(ContentResponse.class);
        DataHelpers mockDH = mock(DataHelpers.class);

        when(mockAC.babbage()).thenReturn(mockBabbage);

        DataHelpers.isNavigation = true;
        DataHelpers.isPublication = false;

        assertTrue(DataHelpers.isNavigation);
        assertFalse(DataHelpers.isPublication);


//        build options
        File file = new File("src/test/resources/TestSectiondata.json");
        JsonParser parser = new JsonParser();
        Object uri = parser.parse(new java.io.FileReader(file));
        Options options = setOptions();
        assertEquals(options.helperName, "resolveTaxonomy");
        assertTrue( options.<Integer>hash("depth") == 2);

        String original = "{\"description\":\"A list of topical areas and their subtopics in english to generate the website navbar.\",\"links\":{\"self\":{\"href\":\"/navigation\"}},\"items\":[{\"description\":\"Activities of businesses and industry in the UK, including data on the production and trade of goods and services, sales by retailers, characteristics of businesses, the construction and manufacturing sectors, and international trade.\",\"label\":\"Business, industry and trade\",\"links\":{\"self\":{\"href\":\"/topics/businessindustryandtrade\",\"id\":\"businessindustryandtrade\"}},\"name\":\"business-industry-and-trade\",\"subtopics\":[{\"description\":\"UK businesses registered for VAT and PAYE with regional breakdowns, including data on size (employment and turnover) and activity (type of industry), research and development, and business services.\",\"label\":\"Business\",\"links\":{\"self\":{\"href\":\"/topics/business\",\"id\":\"business\"}},\"name\":\"business\",\"title\":\"Business\",\"uri\":\"/businessindustryandtrade/business\"},{\"description\":\"UK business growth, survival and change over time. These figures are an informal indicator of confidence in the UK economy.\",\"label\":\"Changes to business\",\"links\":{\"self\":{\"href\":\"/topics/changestobusiness\",\"id\":\"changestobusiness\"}},\"name\":\"changes-to-business\",\"title\":\"Changes to business\",\"uri\":\"/businessindustryandtrade/changestobusiness\"},{\"description\":\"Construction of new buildings and repairs or alterations to existing properties in Great Britain measured by the amount charged for the work, including work by civil engineering companies. \",\"label\":\"Construction industry\",\"links\":{\"self\":{\"href\":\"/topics/constructionindustry\",\"id\":\"constructionindustry\"}},\"name\":\"construction-industry\",\"title\":\"Construction industry\",\"uri\":\"/businessindustryandtrade/constructionindustry\"},{\"description\":\"Internet sales by businesses in the UK (total value and as a percentage of all retail sales) and the percentage of businesses that have a website and broadband connection. These figures indicate the importance of the internet to UK businesses.\",\"label\":\"IT and internet industry\",\"links\":{\"self\":{\"href\":\"/topics/itandinternetindustry\",\"id\":\"itandinternetindustry\"}},\"name\":\"it-and-internet-industry\",\"title\":\"IT and internet industry\",\"uri\":\"/businessindustryandtrade/itandinternetindustry\"},{\"description\":\"Trade in goods and services across the UK's international borders, including total imports and exports, the types of goods and services traded and general trends in international trade. \",\"label\":\"International trade\",\"links\":{\"self\":{\"href\":\"/topics/internationaltrade\",\"id\":\"internationaltrade\"}},\"name\":\"international-trade\",\"title\":\"International trade\",\"uri\":\"/businessindustryandtrade/internationaltrade\"},{\"description\":\"UK manufacturing and other production industries (such as mining and quarrying, energy supply, water supply and waste management), including total UK production output, and UK manufactures' sales by product and industrial division, with EU comparisons.\",\"label\":\"Manufacturing and production industry\",\"links\":{\"self\":{\"href\":\"/topics/manufacturingandproductionindustry\",\"id\":\"manufacturingandproductionindustry\"}},\"name\":\"manufacturing-and-production-industry\",\"title\":\"Manufacturing and production industry\",\"uri\":\"/businessindustryandtrade/manufacturingandproductionindustry\"},{\"label\":\"Retail industry\",\"links\":{\"self\":{\"href\":\"/topics/retailindustry\",\"id\":\"retailindustry\"}},\"name\":\"retail-industry\",\"title\":\"Retail industry\",\"uri\":\"/businessindustryandtrade/retailindustry\"},{\"description\":\"Tourism and travel (including accommodation services, food and beverage services, passenger transport services, vehicle hire, travel agencies and sports, recreational and conference services), employment levels and output of the tourism industry, the number of visitors to the UK and the amount they spend.\",\"label\":\"Tourism industry\",\"links\":{\"self\":{\"href\":\"/topics/tourismindustry\",\"id\":\"tourismindustry\"}},\"name\":\"tourism-industry\",\"title\":\"Tourism industry\",\"uri\":\"/businessindustryandtrade/tourismindustry\"}],\"title\":\"Business, industry and trade\",\"uri\":\"/businessindustryandtrade\"},{\"description\":\"UK economic activity covering production, distribution, consumption and trade of goods and services. Individuals, businesses, organisations and governments all affect the development of the economy.\",\"label\":\"Economy\",\"links\":{\"self\":{\"href\":\"/topics/economy\",\"id\":\"economy\"}},\"name\":\"economy\",\"subtopics\":[{\"description\":\"Manufacturing, production and services indices (measuring total economic output) and productivity (measuring efficiency, expressed as a ratio of output to input over a given period of time, for example output per person per hour).\",\"label\":\"Economic output and productivity\",\"links\":{\"self\":{\"href\":\"/topics/economicoutputandproductivity\",\"id\":\"economicoutputandproductivity\"}},\"name\":\"economic-output-and-productivity\",\"title\":\"Economic output and productivity\",\"uri\":\"/economy/economicoutputandproductivity\"},{\"description\":\"Environmental accounts show how the environment contributes to the economy (for example, through the extraction of raw materials), the impacts that the economy has on the environment (for example, energy consumption and air emissions), and how society responds to environmental issues (for example, through taxation and expenditure on environmental protection).\",\"label\":\"Environmental accounts\",\"links\":{\"self\":{\"href\":\"/topics/environmentalaccounts\",\"id\":\"environmentalaccounts\"}},\"name\":\"environmental-accounts\",\"title\":\"Environmental accounts\",\"uri\":\"/economy/environmentalaccounts\"},{\"description\":\"Public sector spending, tax revenues and investments for the UK, including government debt and deficit (the gap between revenue and spending), research and development, and the effect of taxes.\",\"label\":\"Government, public sector and taxes\",\"links\":{\"self\":{\"href\":\"/topics/governmentpublicsectorandtaxes\",\"id\":\"governmentpublicsectorandtaxes\"}},\"name\":\"government-public-sector-and-taxes\",\"title\":\"Government, public sector and taxes\",\"uri\":\"/economy/governmentpublicsectorandtaxes\"},{\"description\":\"Estimates of GDP are released on a monthly and quarterly basis. Monthly estimates are released alongside other short-term economic indicators. The two quarterly estimates contain data from all three approaches to measuring GDP and are called the First quarterly estimate of GDP and the Quarterly National Accounts. Data sources feeding into the two types of releases are consistent with each other.\",\"label\":\"Gross Domestic Product (GDP)\",\"links\":{\"self\":{\"href\":\"/topics/grossdomesticproductgdp\",\"id\":\"grossdomesticproductgdp\"}},\"name\":\"gross-domestic-product-gdp\",\"title\":\"Gross Domestic Product (GDP)\",\"uri\":\"/economy/grossdomesticproductgdp\"},{\"description\":\"Regional gross value added using production (GVA(P)) and income (GVA(I)) approaches. Regional gross value added is the value generated by any unit engaged in the production of goods and services. GVA per head is a useful way of comparing regions of different sizes. It is not, however, a measure of regional productivity.\",\"label\":\"Gross Value Added (GVA)\",\"links\":{\"self\":{\"href\":\"/topics/grossvalueaddedgva\",\"id\":\"grossvalueaddedgva\"}},\"name\":\"gross-value-added-gva\",\"title\":\"Gross Value Added (GVA)\",\"uri\":\"/economy/grossvalueaddedgva\"},{\"description\":\"The rate of increase in prices for goods and services. Measures of inflation and prices include consumer price inflation, producer price inflation, the house price index, index of private housing rental prices, and construction output price indices. \",\"label\":\"Inflation and price indices\",\"links\":{\"self\":{\"href\":\"/topics/inflationandpriceindices\",\"id\":\"inflationandpriceindices\"}},\"name\":\"inflation-and-price-indices\",\"title\":\"Inflation and price indices\",\"uri\":\"/economy/inflationandpriceindices\"},{\"description\":\"Net flows of investment into the UK, the number of people who hold pensions of different types, and investments made by various types of trusts. \",\"label\":\"Investments, pensions and trusts\",\"links\":{\"self\":{\"href\":\"/topics/investmentspensionsandtrusts\",\"id\":\"investmentspensionsandtrusts\"}},\"name\":\"investments-pensions-and-trusts\",\"title\":\"Investments, pensions and trusts\",\"uri\":\"/economy/investmentspensionsandtrusts\"},{\"description\":\"Core accounts for the UK economy as a whole; individual sectors (sector accounts); accounts for the regions, subregions and local areas of the UK; and satellite accounts that cover activities linked to the economy. The national accounts framework brings units and transactions together to provide a simple and understandable description of production, income, consumption, accumulation and wealth.\",\"label\":\"National accounts\",\"links\":{\"self\":{\"href\":\"/topics/nationalaccounts\",\"id\":\"nationalaccounts\"}},\"name\":\"national-accounts\",\"title\":\"National accounts\",\"uri\":\"/economy/nationalaccounts\"},{\"description\":\"Accounts for regions, sub-regions and local areas of the UK. These accounts allow comparisons between regions and against a UK average. Statistics include regional gross value added (GVA) and figures on regional gross disposable household income (GDHI).\",\"label\":\"Regional accounts\",\"links\":{\"self\":{\"href\":\"/topics/regionalaccounts\",\"id\":\"regionalaccounts\"}},\"name\":\"regional-accounts\",\"title\":\"Regional accounts\",\"uri\":\"/economy/regionalaccounts\"}],\"title\":\"Economy\",\"uri\":\"/economy\"},{\"description\":\"People in and out of work covering employment, unemployment, types of work, earnings, working patterns and workplace disputes.\",\"label\":\"Employment and labour market\",\"links\":{\"self\":{\"href\":\"/topics/employmentandlabourmarket\",\"id\":\"employmentandlabourmarket\"}},\"name\":\"employment-and-labour-market\",\"subtopics\":[{\"description\":\"Employment data covering employment rates, hours of work, claimants and earnings.\",\"label\":\"People in work\",\"links\":{\"self\":{\"href\":\"/topics/peopleinwork\",\"id\":\"peopleinwork\"}},\"name\":\"people-in-work\",\"title\":\"People in work\",\"uri\":\"/employmentandlabourmarket/peopleinwork\"},{\"description\":\"Unemployed and economically inactive people in the UK including claimants of out-of-work benefits and the number of redundancies.\\n\",\"label\":\"People not in work\",\"links\":{\"self\":{\"href\":\"/topics/peoplenotinwork\",\"id\":\"peoplenotinwork\"}},\"name\":\"people-not-in-work\",\"title\":\"People not in work\",\"uri\":\"/employmentandlabourmarket/peoplenotinwork\"}],\"title\":\"Employment and labour market\",\"uri\":\"/employmentandlabourmarket\"},{\"description\":\"People living in the UK, changes in the population, how we spend our money, and data on crime, relationships, health and religion. These statistics help us build a detailed picture of how we live.\",\"label\":\"People, population and community\",\"links\":{\"self\":{\"href\":\"/topics/peoplepopulationandcommunity\",\"id\":\"peoplepopulationandcommunity\"}},\"name\":\"people-population-and-community\",\"subtopics\":[{\"description\":\"Information about the armed forces community, including those who have previously served in the armed forces (veterans) and their families, to help support the Armed Forces Covenant.\",\"label\":\"Armed forces community\",\"links\":{\"self\":{\"href\":\"/topics/armedforcescommunity\",\"id\":\"armedforcescommunity\"}},\"name\":\"armed-forces-community\",\"title\":\"Armed forces community\",\"uri\":\"/peoplepopulationandcommunity/armedforcescommunity\"},{\"description\":\"Life events in the UK including fertility rates, live births and stillbirths, family composition, life expectancy and deaths. This tells us about the health and relationships of the population.\",\"label\":\"Births, deaths and marriages\",\"links\":{\"self\":{\"href\":\"/topics/birthsdeathsandmarriages\",\"id\":\"birthsdeathsandmarriages\"}},\"name\":\"births-deaths-and-marriages\",\"title\":\"Births, deaths and marriages\",\"uri\":\"/peoplepopulationandcommunity/birthsdeathsandmarriages\"},{\"description\":\"Crimes committed and the victims' characteristics, sourced from crimes recorded by the police and from the Crime Survey for England and Wales (CSEW). The outcomes of crime in terms of what happened to the offender are also included.\",\"label\":\"Crime and justice\",\"links\":{\"self\":{\"href\":\"/topics/crimeandjustice\",\"id\":\"crimeandjustice\"}},\"name\":\"crime-and-justice\",\"title\":\"Crime and justice\",\"uri\":\"/peoplepopulationandcommunity/crimeandjustice\"},{\"description\":\"How people in the UK see themselves today in terms of ethnicity, sexual identity, religion and language, and how this has changed over time. We use a diverse range of sources for this data.\",\"label\":\"Cultural identity\",\"links\":{\"self\":{\"href\":\"/topics/culturalidentity\",\"id\":\"culturalidentity\"}},\"name\":\"cultural-identity\",\"title\":\"Cultural identity\",\"uri\":\"/peoplepopulationandcommunity/culturalidentity\"},{\"description\":\"Early years childcare, school and college education, and higher education and adult learning, including qualifications, personnel, and safety and well-being. \",\"label\":\"Education and childcare\",\"links\":{\"self\":{\"href\":\"/topics/educationandchildcare\",\"id\":\"educationandchildcare\"}},\"name\":\"education-and-childcare\",\"title\":\"Education and childcare\",\"uri\":\"/peoplepopulationandcommunity/educationandchildcare\"},{\"label\":\"Elections\",\"links\":{\"self\":{\"href\":\"/topics/elections\",\"id\":\"elections\"}},\"name\":\"elections\",\"title\":\"Elections\",\"uri\":\"/peoplepopulationandcommunity/elections\"},{\"description\":\"Life expectancy and the impact of factors such as occupation, illness and drug misuse. We collect these statistics from registrations and surveys. \",\"label\":\"Health and social care\",\"links\":{\"self\":{\"href\":\"/topics/healthandsocialcare\",\"id\":\"healthandsocialcare\"}},\"name\":\"health-and-social-care\",\"title\":\"Health and social care\",\"uri\":\"/peoplepopulationandcommunity/healthandsocialcare\"},{\"description\":\"The composition of households, including those who live alone, overcrowding and under-occupation, as well as internet and social media usage by household.\",\"label\":\"Household characteristics\",\"links\":{\"self\":{\"href\":\"/topics/householdcharacteristics\",\"id\":\"householdcharacteristics\"}},\"name\":\"household-characteristics\",\"title\":\"Household characteristics\",\"uri\":\"/peoplepopulationandcommunity/householdcharacteristics\"},{\"description\":\"Property price, private rent and household survey and census statistics, used by government and other organisations for the creation and fulfilment of housing policy in the UK.\",\"label\":\"Housing\",\"links\":{\"self\":{\"href\":\"/topics/housing\",\"id\":\"housing\"}},\"name\":\"housing\",\"title\":\"Housing\",\"uri\":\"/peoplepopulationandcommunity/housing\"},{\"description\":\"Visits and visitors to the UK, the reasons for visiting and the amount of money they spent here. Also UK residents travelling abroad, their reasons for travel and the amount of money they spent. The statistics on UK residents travelling abroad are an informal indicator of living standards.\",\"label\":\"Leisure and tourism\",\"links\":{\"self\":{\"href\":\"/topics/leisureandtourism\",\"id\":\"leisureandtourism\"}},\"name\":\"leisure-and-tourism\",\"title\":\"Leisure and tourism\",\"uri\":\"/peoplepopulationandcommunity/leisureandtourism\"},{\"description\":\"Earnings and household spending, including household and personal debt, expenditure, and income and wealth. These statistics help build a picture of our spending and saving decisions. \",\"label\":\"Personal and household finances\",\"links\":{\"self\":{\"href\":\"/topics/personalandhouseholdfinances\",\"id\":\"personalandhouseholdfinances\"}},\"name\":\"personal-and-household-finances\",\"title\":\"Personal and household finances\",\"uri\":\"/peoplepopulationandcommunity/personalandhouseholdfinances\"},{\"description\":\"Size, age, sex and geographic distribution of the UK population, and changes in the UK population and the factors driving these changes. These statistics have a wide range of uses. Central government, local government and the health sector use them for planning, resource allocation and managing the economy. They are also used by people such as market researchers and academics.\",\"label\":\"Population and migration\",\"links\":{\"self\":{\"href\":\"/topics/populationandmigration\",\"id\":\"populationandmigration\"}},\"name\":\"population-and-migration\",\"title\":\"Population and migration\",\"uri\":\"/peoplepopulationandcommunity/populationandmigration\"},{\"description\":\"Societal and personal well-being in the UK looking beyond what we produce, to areas such as health, relationships, education and skills, what we do, where we live, our finances and the environment. This data comes from a variety of sources and much of the analysis is new.\",\"label\":\"Well-being\",\"links\":{\"self\":{\"href\":\"/topics/wellbeing\",\"id\":\"wellbeing\"}},\"name\":\"wellbeing\",\"title\":\"Well-being\",\"uri\":\"/peoplepopulationandcommunity/wellbeing\"}],\"title\":\"People, population and community\",\"uri\":\"/peoplepopulationandcommunity\"},{\"label\":\"Census\",\"links\":{\"self\":{\"href\":\"/topics/census\",\"id\":\"census\"}},\"name\":\"census\",\"title\":\"Census\",\"uri\":\"/census\"},{\"label\":\"Taking part in a survey?\",\"name\":\"taking-part-in-a-survey\",\"title\":\"Survey\",\"uri\":\"/surveys\"}]}";
        java.io.InputStream inputStream = new java.io.ByteArrayInputStream(original.getBytes());
        Map<String, Object> mapData = com.github.onsdigital.babbage.util.json.JsonUtil.toMap(inputStream);
        List<Map<String, Object>> navigationContext = TaxonomyRenderer.navigationToTaxonomy(mapData.get("items"));
        when(mockStream.getDataStream()).thenReturn(inputStream);
        when(mockClient.getNavigation(depth(2))).thenReturn(mockStream);
        when(mockJU.toMap(inputStream)).thenReturn(mapData);
        CharSequence resolve = mockDH.resolveTaxonomy.apply(uri, options);
        assertTrue(resolve.toString().isEmpty());

    }

    private Options setOptions() throws Exception {
        Map<String, String> uriMock = new LinkedHashMap<>();
        uriMock.put("uri", "/navigation");
        Map<String, Object> optionMap = new HashMap<String,Object>();
        optionMap.put("depth", 2);

        Handlebars handlebars = mock(Handlebars.class);
        TagType tagType = mock(TagType.class);
        Template template = mock(Template.class);

        Context.Builder parentContext = Context.newBuilder(uriMock);

        Context.Builder contextBuilder = Context.newBuilder(parentContext.build(), "test");

        Options.Builder builder = new Options.Builder(
                handlebars,
                "resolveTaxonomy",
                tagType,
                contextBuilder.build(),
                template);
        builder.setHash(optionMap);
        return builder.build();
    }

}