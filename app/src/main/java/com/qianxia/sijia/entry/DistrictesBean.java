package com.qianxia.sijia.entry;

import java.util.List;

/**
 * Created by tarena on 2016/10/11.
 */
public class DistrictesBean {


    private String status;//: "OK",
    private List<City> cities;//: [

    public DistrictesBean() {
        super();
        // TODO Auto-generated constructor stub
    }

    public DistrictesBean(String status, List<City> cities) {
        super();
        this.status = status;
        this.cities = cities;
    }

    @Override
    public String toString() {
        return "DistrictesBean [status=" + status + ", cities=" + cities + "]";
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<City> getCities() {
        return cities;
    }

    public void setCities(List<City> cities) {
        this.cities = cities;
    }

    public static class City {
        private String city_name;//: "上海",
        private List<District> districts;//

        @Override
        public String toString() {
            return "City [city_name=" + city_name + ", districts=" + districts + "]";
        }

        public City() {
            super();
            // TODO Auto-generated constructor stub
        }

        public City(String city_name, List<District> districts) {
            super();
            this.city_name = city_name;
            this.districts = districts;
        }

        public String getCity_name() {
            return city_name;
        }

        public void setCity_name(String city_name) {
            this.city_name = city_name;
        }

        public List<District> getDistricts() {
            return districts;
        }

        public void setDistricts(List<District> districts) {
            this.districts = districts;
        }

        public static class District {
            private String district_name;//: "卢湾区",
            private List<String> neighborhoods;

            public String getDistrict_name() {
                return district_name;
            }

            public void setDistrict_name(String district_name) {
                this.district_name = district_name;
            }

            public List<String> getNeighborhoods() {
                return neighborhoods;
            }

            public void setNeighborhoods(List<String> neighborhoods) {
                this.neighborhoods = neighborhoods;
            }

            public District(String district_name, List<String> neighborhoods) {
                super();
                this.district_name = district_name;
                this.neighborhoods = neighborhoods;
            }

            public District() {
                super();
                // TODO Auto-generated constructor stub
            }

            @Override
            public String toString() {
                return "District [district_name=" + district_name + ", neighborhoods=" + neighborhoods + "]";
            }

        }
    }


}
